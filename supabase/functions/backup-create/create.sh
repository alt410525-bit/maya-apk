#!/usr/bin/env bash
# Supabase Edge Function: backup/create
# Encrypted backup upload to Supabase Storage

echo "Creating backup/create Edge Function..."

cat > /root/maya_clone_project/supabase/functions/backup-create/index.ts << 'EOF'
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

    const { backup_type, encrypted_data, size_bytes } = await req.json()

    if (!encrypted_data) {
      return new Response(JSON.stringify({ error: 'No backup data provided' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Create backup job record
    const { data: backupJob, error: jobError } = await supabaseClient
      .from('backup_jobs')
      .insert({
        user_id: user.id,
        type: backup_type || 'full',
        status: 'running',
        size_bytes: size_bytes || encrypted_data.length,
        started_at: new Date().toISOString(),
      })
      .select()
      .single()

    if (jobError || !backupJob) {
      return new Response(JSON.stringify({ error: 'Failed to create backup job' }), { 
        status: 500, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Upload to Supabase Storage
    const storagePath = `backups/${user.id}/${backupJob.id}.enc`
    const { error: uploadError } = await supabaseClient.storage
      .from('backups')
      .upload(storagePath, encrypted_data, {
        contentType: 'application/octet-stream',
        upsert: true,
      })

    if (uploadError) {
      await supabaseClient
        .from('backup_jobs')
        .update({ status: 'failed', error_message: uploadError.message })
        .eq('id', backupJob.id)

      return new Response(JSON.stringify({ error: 'Upload failed' }), { 
        status: 500, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Update job as completed
    await supabaseClient
      .from('backup_jobs')
      .update({ 
        status: 'completed',
        storage_path: storagePath,
        completed_at: new Date().toISOString(),
      })
      .eq('id', backupJob.id)

    return new Response(JSON.stringify({ 
      job_id: backupJob.id,
      storage_path: storagePath,
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

echo "Edge Function created: backup-create"
EOF
chmod +x /root/maya_clone_project/supabase/functions/backup-create/create.sh
/root/maya_clone_project/supabase/functions/backup-create/create.sh