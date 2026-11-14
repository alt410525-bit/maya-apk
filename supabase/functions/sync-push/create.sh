#!/usr/bin/env bash
# Supabase Edge Function: sync/push
# Push local changes to server

echo "Creating sync/push Edge Function..."

cat > /root/maya_clone_project/supabase/functions/sync-push/index.ts << 'EOF'
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

    const { changes } = await req.json()

    if (!changes || typeof changes !== 'object') {
      return new Response(JSON.stringify({ error: 'Invalid changes payload' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    const results: Record<string, { inserted: number, updated: number, errors: string[] }> = {}

    for (const table of Object.keys(changes)) {
      if (!['agents', 'conversations', 'messages', 'api_keys', 'face_profiles',
        'face_lock_attempts', 'backup_jobs', 'auto_reply_rules', 'awareness_events',
        'triggers', 'encryption_keys', 'connectors', 'themes', 'licenses', 'settings'].includes(table)) {
        continue
      }

      const tableChanges = changes[table]
      if (!Array.isArray(tableChanges)) continue

      let inserted = 0
      let updated = 0
      const errors: string[] = []

      for (const record of tableChanges) {
        try {
          const { data, error } = await supabaseClient
            .from(table)
            .upsert({ ...record, user_id: user.id }, { onConflict: 'id' })
            .select('id')

          if (error) {
            errors.push(`${record.id}: ${error.message}`)
          } else if (data) {
            // Check if it was insert or update by checking created_at vs updated_at
            // Simplified: count all as updated for now
            updated++
          }
        } catch (e) {
          errors.push(`${record.id}: ${e.message}`)
        }
      }

      results[table] = { inserted, updated, errors }
    }

    const serverTimestamp = Date.now()

    return new Response(JSON.stringify({ 
      results,
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

echo "Edge Function created: sync-push"
EOF
chmod +x /root/maya_clone_project/supabase/functions/sync-push/create.sh
/root/maya_clone_project/supabase/functions/sync-push/create.sh