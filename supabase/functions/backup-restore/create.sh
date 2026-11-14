#!/usr/bin/env bash
# Supabase Edge Function: backup/restore
# Download and decrypt backup from Supabase Storage

echo "Creating backup/restore Edge Function..."

cat > /root/maya_clone_project/supabase/functions/backup-restore/index.ts << 'EOF'
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

    const { job_id } = await req.json()

    if (!job_id) {
      return new Response(JSON.stringify({ error: 'Missing job_id' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Get backup job
    const { data: backupJob, error: jobError } = await supabaseClient
      .from('backup_jobs')
      .select('*')
      .eq('id', job_id)
      .eq('user_id', user.id)
      .single()

    if (jobError || !backupJob) {
      return new Response(JSON.stringify({ error: 'Backup job not found' }), { 
        status: 404, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    if (backupJob.status !== 'completed') {
      return new Response(JSON.stringify({ error: 'Backup not completed' }), { 
        status: 400, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    // Download from Storage
    const { data: fileData, error: downloadError } = await supabaseClient.storage
      .from('backups')
      .download(backupJob.storage_path)

    if (downloadError || !fileData) {
      return new Response(JSON.stringify({ error: 'Download failed' }), { 
        status: 500, 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
      })
    }

    const arrayBuffer = await fileData.arrayBuffer()
    const bytes = new Uint8Array(arrayBuffer)

    return new Response(bytes, { 
      status: 200, 
      headers: { 
        ...corsHeaders, 
        'Content-Type': 'application/octet-stream',
        'Content-Length': bytes.length.toString(),
        'Content-Disposition': `attachment; filename="backup_${job_id}.enc"`,
      } 
    })

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), { 
      status: 500, 
      headers: { ...corsHeaders, 'Content-Type': 'application/json' } 
    })
  }
})
EOF

echo "Edge Function created: backup-restore"
EOF
chmod +x /root/maya_clone_project/supabase/functions/backup-restore/create.sh
/root/maya_clone_project/supabase/functions/backup-restore/create.sh