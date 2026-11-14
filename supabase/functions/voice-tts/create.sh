#!/usr/bin/env bash
# Supabase Edge Function: voice/tts
# Text-to-speech using cloud providers

echo "Creating voice/tts Edge Function..."

cat > /root/maya_clone_project/supabase/functions/voice-tts/index.ts << 'EOF'
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

    const { text, voice, language, engine } = await req.json()

    if (!text) {
      return new Response(JSON.stringify({ error: 'No text provided' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Check for ElevenLabs API key (high quality)
    const { data: elevenKeys } = await supabaseClient
      .from('api_keys')
      .select('provider, key_encrypted')
      .eq('user_id', user.id)
      .eq('provider', 'elevenlabs')

    if (elevenKeys && elevenKeys.length > 0) {
      const elevenKey = elevenKeys[0].key_encrypted
      const voiceId = voice || '21m00Tcm4TlvDq8ikWAM' // Default: Rachel

      const elevenResponse = await fetch(
        `https://api.elevenlabs.io/v1/text-to-speech/${voiceId}`,
        {
          method: 'POST',
          headers: {
            'xi-api-key': elevenKey,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            text,
            model_id: 'eleven_monolingual_v1',
            voice_settings: {
              stability: 0.5,
              similarity_boost: 0.75,
            },
          }),
        }
      )

      if (elevenResponse.ok) {
        const audioBuffer = await elevenResponse.arrayBuffer()
        return new Response(audioBuffer, { 
          status: 200, 
          headers: { 
            ...corsHeaders, 
            'Content-Type': 'audio/mpeg',
            'Content-Length': audioBuffer.byteLength.toString(),
          } 
        })
      }
    }

    // Fallback: Google Cloud TTS
    const { data: googleKeys } = await supabaseClient
      .from('api_keys')
      .select('provider, key_encrypted')
      .eq('user_id', user.id)
      .eq('provider', 'google')

    if (googleKeys && googleKeys.length > 0) {
      const googleKey = googleKeys[0].key_encrypted

      const googleResponse = await fetch(
        `https://texttospeech.googleapis.com/v1/text:synthesize?key=${googleKey}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            input: { text },
            voice: { 
              languageCode: language || 'en-US', 
              name: voice || 'en-US-Neural2-F' 
            },
            audioConfig: { audioEncoding: 'MP3' },
          }),
        }
      )

      if (googleResponse.ok) {
        const result = await googleResponse.json()
        const audioBase64 = result.audioContent
        const audioBytes = Uint8Array.from(atob(audioBase64), c => c.charCodeAt(0))
        return new Response(audioBytes, { 
          status: 200, 
          headers: { 
            ...corsHeaders, 
            'Content-Type': 'audio/mpeg',
            'Content-Length': audioBytes.length.toString(),
          } 
        })
      }
    }

    // No cloud TTS available - return error for on-device fallback
    return new Response(JSON.stringify({ 
      error: 'No TTS provider configured',
      use_on_device: true 
    }), { 
      status: 400, 
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

echo "Edge Function created: voice-tts"
EOF
chmod +x /root/maya_clone_project/supabase/functions/voice-tts/create.sh
/root/maya_clone_project/supabase/functions/voice-tts/create.sh