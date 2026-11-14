#!/usr/bin/env bash
# Supabase Edge Function: chat/completions
# Proxies LLM API calls to hide API keys

echo "Creating chat/completions Edge Function..."

cat > /root/maya_clone_project/supabase/functions/chat-completions/index.ts << 'EOF'
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const supabaseClient = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_ANON_KEY') ?? '',
      { global: { headers: { Authorization: req.headers.get('Authorization')! } } }
    )

    const { data: { user } } = await supabaseClient.auth.getUser()
    if (!user) {
      return new Response(JSON.stringify({ error: 'Unauthorized' }), { 
        status: 401, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    const { messages, model, temperature, stream, tools } = await req.json()

    // Get user's API keys
    const { data: apiKeys } = await supabaseClient
      .from('api_keys')
      .select('provider, key_encrypted')
      .eq('user_id', user.id)

    if (!apiKeys || apiKeys.length === 0) {
      return new Response(JSON.stringify({ error: 'No API keys configured' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Decrypt the appropriate key (simplified - in production use proper encryption)
    const openaiKey = apiKeys.find(k => k.provider === 'openai')?.key_encrypted
    if (!openaiKey) {
      return new Response(JSON.stringify({ error: 'OpenAI key not found' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Call OpenAI API
    const openaiResponse = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${openaiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        model: model || 'gpt-4o-mini',
        messages,
        temperature: temperature ?? 0.7,
        stream: stream ?? false,
        tools,
      }),
    })

    if (!openaiResponse.ok) {
      const error = await openaiResponse.text()
      return new Response(JSON.stringify({ error }), { 
        status: openaiResponse.status, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Update usage stats
    await supabaseClient
      .from('api_keys')
      .update({ last_used_at: new Date().toISOString() })
      .eq('provider', 'openai')
      .eq('user_id', user.id)

    // Return response (stream or regular)
    const responseHeaders = new Headers(corsHeaders)
    if (stream) {
      responseHeaders.set('Content-Type', 'text/event-stream')
      responseHeaders.set('Cache-Control', 'no-cache')
      responseHeaders.set('Connection', 'keep-alive')
    } else {
      responseHeaders.set('Content-Type', 'application/json')
    }

    return new Response(openaiResponse.body, { 
      status: openaiResponse.status, 
      headers: responseHeaders 
    })

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), { 
      status: 500, 
      headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
    })
  }
})
EOF

echo "Edge Function created: chat-completions"
EOF
chmod +x /root/maya_clone_project/supabase/functions/chat-completions/create.sh
/root/maya_clone_project/supabase/functions/chat-completions/create.sh