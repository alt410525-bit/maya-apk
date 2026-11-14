#!/usr/bin/env bash
# Supabase Edge Function: sync/pull
# Pull remote changes since timestamp

echo "Creating sync/pull Edge Function..."

cat > /root/maya_clone_project/supabase/functions/sync-pull/index.ts << 'EOF'
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

const TABLES = [
  'agents', 'conversations', 'messages', 'api_keys', 'face_profiles',
  'face_lock_attempts', 'backup_jobs', 'auto_reply_rules', 'awareness_events',
  'triggers', 'encryption_keys', 'connectors', 'themes', 'licenses', 'settings'
]

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

    const { since_timestamp } = await req.json()
    const since = since_timestamp || 0

    const changes: Record<string, any[]> = {}

    for (const table of TABLES) {
      const { data, error } = await supabaseClient
        .from(table)
        .select('*')
        .eq('user_id', user.id)
        .gte('updated_at', since)
        .order('updated_at', { ascending: true })

      if (!error && data) {
        changes[table] = data
      }
    }

    // Get server timestamp for next sync
    const serverTimestamp = Date.now()

    return new Response(JSON.stringify({ 
      changes,
      server_timestamp: serverTimestamp,
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

echo "Edge Function created: sync-pull"
EOF
chmod +x /root/maya_clone_project/supabase/functions/sync-pull/create.sh
/root/maya_clone_project/supabase/functions/sync-pull/create.sh