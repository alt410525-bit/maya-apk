#!/usr/bin/env bash
# Supabase Edge Function: license/verify
# Verify Google Play Billing receipts

echo "Creating license/verify Edge Function..."

cat > /root/maya_clone_project/supabase/functions/license-verify/index.ts << 'EOF'
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

    const { purchase_token, product_id } = await req.json()

    if (!purchase_token) {
      return new Response(JSON.stringify({ error: 'Missing purchase token' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // In production: verify with Google Play Developer API
    // For now, simulate verification
    const tier = product_id?.includes('promax') ? 'promax' : product_id?.includes('pro') ? 'pro' : 'free'

    // Store license
    await supabaseClient
      .from('licenses')
      .upsert({
        user_id: user.id,
        tier,
        purchase_token,
        expires_at: tier === 'free' ? null : new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString(),
        features_json: JSON.stringify(getFeaturesForTier(tier)),
        verified_at: new Date().toISOString(),
      }, { onConflict: 'user_id' })

    return new Response(JSON.stringify({ 
      verified: true,
      tier,
      expires_at: tier === 'free' ? null : new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString(),
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

function getFeaturesForTier(tier: string) {
  const features = {
    free: ['basic_chat', 'basic_voice'],
    pro: ['basic_chat', 'basic_voice', 'advanced_voice', 'sub_agents', 'skills', 'backup', 'face_lock'],
    promax: ['basic_chat', 'basic_voice', 'advanced_voice', 'sub_agents', 'skills', 'backup', 'face_lock', 'connectors', 'auto_reply', 'awareness', 'triggers', 'priority_support'],
  }
  return features[tier] || features.free
}
EOF

echo "Edge Function created: license-verify"
EOF
chmod +x /root/maya_clone_project/supabase/functions/license-verify/create.sh
/root/maya_clone_project/supabase/functions/license-verify/create.sh