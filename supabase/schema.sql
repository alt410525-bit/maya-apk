-- Maya Clone Database Schema for Supabase (PostgreSQL)
-- Converted from SQLite schema for Supabase/PostgreSQL compatibility

-- ============================================
-- EXTENSIONS (Postgres only)
-- ============================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================
-- 1. AGENTS
-- ============================================
CREATE TABLE agents (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  avatar_url TEXT,
  system_prompt TEXT NOT NULL DEFAULT '',
  model TEXT NOT NULL DEFAULT 'gpt-4o-mini',
  temperature REAL NOT NULL DEFAULT 0.7,
  tools_json JSONB NOT NULL DEFAULT '[]',
  permissions_json JSONB NOT NULL DEFAULT '{}',
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_agents_user ON agents(user_id);
CREATE INDEX idx_agents_active ON agents(user_id, is_active);

-- ============================================
-- 2. CONVERSATIONS
-- ============================================
CREATE TABLE conversations (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  agent_id UUID NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
  title TEXT,
  message_count INTEGER NOT NULL DEFAULT 0,
  last_message_at BIGINT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_conversations_user ON conversations(user_id);
CREATE INDEX idx_conversations_agent ON conversations(agent_id);
CREATE INDEX idx_conversations_recent ON conversations(user_id, last_message_at DESC);

-- ============================================
-- 3. MESSAGES
-- ============================================
CREATE TABLE messages (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
  role TEXT NOT NULL CHECK (role IN ('user','assistant','system','tool')),
  content TEXT NOT NULL DEFAULT '',
  attachments_json JSONB NOT NULL DEFAULT '[]',
  metadata_json JSONB NOT NULL DEFAULT '{}',
  status TEXT NOT NULL DEFAULT 'sent' CHECK (status IN ('sending','sent','streaming','failed','cancelled')),
  token_count INTEGER DEFAULT 0,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_messages_conversation ON messages(conversation_id, created_at);
CREATE INDEX idx_messages_status ON messages(status);

-- ============================================
-- 4. API KEYS
-- ============================================
CREATE TABLE api_keys (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  provider TEXT NOT NULL,
  key_hash TEXT NOT NULL,
  key_encrypted TEXT NOT NULL,
  label TEXT,
  scopes_json JSONB NOT NULL DEFAULT '[]',
  last_used_at BIGINT,
  expires_at BIGINT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_api_keys_user ON api_keys(user_id);
CREATE INDEX idx_api_keys_provider ON api_keys(user_id, provider);

-- ============================================
-- 5. FACE PROFILES
-- ============================================
CREATE TABLE face_profiles (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  embedding BYTEA NOT NULL,
  photo_urls_json JSONB NOT NULL DEFAULT '[]',
  quality_score REAL,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_face_profiles_user ON face_profiles(user_id);

-- ============================================
-- 6. FACE LOCK ATTEMPTS
-- ============================================
CREATE TABLE face_lock_attempts (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  success BOOLEAN NOT NULL,
  confidence REAL,
  fallback_used BOOLEAN NOT NULL DEFAULT false,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_face_attempts_user_time ON face_lock_attempts(user_id, created_at DESC);

-- ============================================
-- 7. BACKUP JOBS
-- ============================================
CREATE TABLE backup_jobs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  type TEXT NOT NULL CHECK (type IN ('full','incremental','settings_only','faces_only')),
  status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','running','completed','failed','cancelled')),
  storage_path TEXT,
  size_bytes BIGINT,
  error_message TEXT,
  started_at BIGINT,
  completed_at BIGINT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_backup_jobs_user ON backup_jobs(user_id, created_at DESC);

-- ============================================
-- 8. AUTO-REPLY RULES
-- ============================================
CREATE TABLE auto_reply_rules (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  conditions_json JSONB NOT NULL,
  actions_json JSONB NOT NULL,
  priority INTEGER NOT NULL DEFAULT 0,
  enabled BOOLEAN NOT NULL DEFAULT true,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_autoreply_user ON auto_reply_rules(user_id, enabled, priority DESC);

-- ============================================
-- 9. AWARENESS EVENTS
-- ============================================
CREATE TABLE awareness_events (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  type TEXT NOT NULL,
  payload_json JSONB NOT NULL,
  processed BOOLEAN NOT NULL DEFAULT false,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_awareness_user_time ON awareness_events(user_id, created_at DESC);
CREATE INDEX idx_awareness_type ON awareness_events(user_id, type);

-- ============================================
-- 10. TRIGGERS (AUTOMATION)
-- ============================================
CREATE TABLE triggers (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  event_type TEXT NOT NULL,
  conditions_json JSONB NOT NULL,
  actions_json JSONB NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT true,
  cooldown_ms BIGINT DEFAULT 0,
  last_fired_at BIGINT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_triggers_user ON triggers(user_id, enabled);

-- ============================================
-- 11. ENCRYPTION KEYS
-- ============================================
CREATE TABLE encryption_keys (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  purpose TEXT NOT NULL CHECK (purpose IN ('backup','face_embeddings','api_keys','messages','export')),
  algorithm TEXT NOT NULL DEFAULT 'AES-256-GCM',
  key_material_encrypted TEXT NOT NULL,
  key_version INTEGER NOT NULL DEFAULT 1,
  rotated_at BIGINT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE UNIQUE INDEX uq_encryption_key_user_purpose ON encryption_keys(user_id, purpose, key_version);

-- ============================================
-- 12. CONNECTORS
-- ============================================
CREATE TABLE connectors (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  type TEXT NOT NULL,
  config_json JSONB NOT NULL,
  access_token_encrypted TEXT,
  refresh_token_encrypted TEXT,
  token_expires_at BIGINT,
  status TEXT NOT NULL DEFAULT 'disconnected' CHECK (status IN ('connected','disconnected','error','expired')),
  last_sync_at BIGINT,
  error_message TEXT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_connectors_user ON connectors(user_id, status);

-- ============================================
-- 13. THEMES
-- ============================================
CREATE TABLE themes (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  is_custom BOOLEAN NOT NULL DEFAULT true,
  colors_json JSONB NOT NULL,
  typography_json JSONB NOT NULL,
  spacing_json JSONB NOT NULL,
  shape_json JSONB NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT false,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_themes_user ON themes(user_id, is_active);

-- ============================================
-- 14. LICENSES
-- ============================================
CREATE TABLE licenses (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  tier TEXT NOT NULL CHECK (tier IN ('free','pro','promax')),
  purchase_token TEXT,
  expires_at BIGINT,
  features_json JSONB NOT NULL DEFAULT '[]',
  verified_at BIGINT,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);
CREATE INDEX idx_licenses_user ON licenses(user_id);

-- ============================================
-- 15. SETTINGS (catch-all key-value for simple prefs)
-- ============================================
CREATE TABLE settings (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL,
  key TEXT NOT NULL,
  value_json JSONB NOT NULL,
  created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  updated_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
  UNIQUE(user_id, key)
);
CREATE INDEX idx_settings_user ON settings(user_id);

-- ============================================
-- ROW LEVEL SECURITY
-- ============================================
-- Enable RLS on all tables
ALTER TABLE agents ENABLE ROW LEVEL SECURITY;
ALTER TABLE conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE api_keys ENABLE ROW LEVEL SECURITY;
ALTER TABLE face_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE face_lock_attempts ENABLE ROW LEVEL SECURITY;
ALTER TABLE backup_jobs ENABLE ROW LEVEL SECURITY;
ALTER TABLE auto_reply_rules ENABLE ROW LEVEL SECURITY;
ALTER TABLE awareness_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE triggers ENABLE ROW LEVEL SECURITY;
ALTER TABLE encryption_keys ENABLE ROW LEVEL SECURITY;
ALTER TABLE connectors ENABLE ROW LEVEL SECURITY;
ALTER TABLE themes ENABLE ROW LEVEL SECURITY;
ALTER TABLE licenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE settings ENABLE ROW LEVEL SECURITY;

-- Policy: users only see their own data
CREATE POLICY "own_data" ON agents FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON conversations FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON messages FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON api_keys FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON face_profiles FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON face_lock_attempts FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON backup_jobs FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON auto_reply_rules FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON awareness_events FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON triggers FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON encryption_keys FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON connectors FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON themes FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON licenses FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "own_data" ON settings FOR ALL USING (auth.uid() = user_id);

-- ============================================
-- TRIGGERS FOR updated_at (Postgres only)
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = EXTRACT(EPOCH FROM NOW()) * 1000;
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_agents_updated_at BEFORE UPDATE ON agents FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_conversations_updated_at BEFORE UPDATE ON conversations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_messages_updated_at BEFORE UPDATE ON messages FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_api_keys_updated_at BEFORE UPDATE ON api_keys FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_face_profiles_updated_at BEFORE UPDATE ON face_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_backup_jobs_updated_at BEFORE UPDATE ON backup_jobs FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_auto_reply_rules_updated_at BEFORE UPDATE ON auto_reply_rules FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_triggers_updated_at BEFORE UPDATE ON triggers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_encryption_keys_updated_at BEFORE UPDATE ON encryption_keys FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_connectors_updated_at BEFORE UPDATE ON connectors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_themes_updated_at BEFORE UPDATE ON themes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_licenses_updated_at BEFORE UPDATE ON licenses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_settings_updated_at BEFORE UPDATE ON settings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();