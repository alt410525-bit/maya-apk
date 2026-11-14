#!/usr/bin/env bash
# Supabase Edge Function: face/verify
# Face verification using cloud or ML Kit

echo "Creating face/verify Edge Function..."

cat > /root/maya_clone_project/supabase/functions/face-verify/index.ts << 'EOF'
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
    const imageFile = formData.get('image') as File
    const faceId = formData.get('face_id') as string

    if (!imageFile || !faceId) {
      return new Response(JSON.stringify({ error: 'Missing image or face_id' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Get stored face profile
    const { data: faceProfile } = await supabaseClient
      .from('face_profiles')
      .select('embedding')
      .eq('id', faceId)
      .eq('user_id', user.id)
      .single()

    if (!faceProfile) {
      return new Response(JSON.stringify({ error: 'Face profile not found' }), { 
        status: 404, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // In production: compare image embedding with stored embedding
    // For now, return mock verification result
    return new Response(JSON.stringify({ 
      verified: true,
      confidence: 0.92,
      method: 'cloud_backup'
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

echo "Edge Function created: face-verify"
EOF
chmod +x /root/maya_clone_project/supabase/functions/face-verify/create.sh
/root/maya_clone_project/supabase/functions/face-verify/create.sh