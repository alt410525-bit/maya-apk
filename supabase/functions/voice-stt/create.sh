#!/usr/bin/env bash
# Supabase Edge Function: voice/stt
# Speech-to-text using ML Kit or cloud fallback

echo "Creating voice/stt Edge Function..."

cat > /root/maya_clone_project/supabase/functions/voice-stt/index.ts << 'EOF'
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

    const formData = await req.formData()
    const audioFile = formData.get('audio') as File
    const language = formData.get('language') as string || 'en-US'

    if (!audioFile) {
      return new Response(JSON.stringify({ error: 'No audio file provided' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Check for Google Cloud Speech API key
    const { data: apiKeys } = await supabaseClient
      .from('api_keys')
      .select('provider, key_encrypted')
      .eq('user_id', user.id)
      .eq('provider', 'google')

    if (!apiKeys || apiKeys.length === 0) {
      // Fallback: return mock response for on-device ML Kit
      return new Response(JSON.stringify({ 
        text: '[On-device STT would process here]',
        confidence: 0.9,
        is_on_device: true
      }), { 
        status: 200, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Use Google Cloud Speech-to-Text
    const googleKey = apiKeys[0].key_encrypted
    const audioBytes = await audioFile.arrayBuffer()
    const base64Audio = btoa(String.fromCharCode(...new Uint8Array(audioBytes)))

    const googleResponse = await fetch(
      `https://speech.googleapis.com/v1/speech:recognize?key=${googleKey}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          config: {
            encoding: 'WEBM_OPUS',
            sampleRateHertz: 48000,
            languageCode: language,
            enableAutomaticPunctuation: true,
          },
          audio: { content: base64Audio },
        }),
      }
    )

    if (!googleResponse.ok) {
      const error = await googleResponse.text()
      return new Response(JSON.stringify({ error }), { 
        status: googleResponse.status, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    const result = await googleResponse.json()
    const transcript = result.results?.[0]?.alternatives?.[0]?.transcript || ''
    const confidence = result.results?.[0]?.alternatives?.[0]?.confidence || 0

    return new Response(JSON.stringify({ 
      text: transcript,
      confidence,
      is_on_device: false
    }), { 
      status: 200, 
      headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
    })

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), { 
      status: 500, 
      headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
    })
  }
})
EOF

echo "Edge Function created: voice-stt"
EOF
chmod +x /root/maya_clone_project/supabase/functions/voice-stt/create.sh
/root/maya_clone_project/supabase/functions/voice-stt/create.sh