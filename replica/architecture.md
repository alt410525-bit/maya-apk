# Maya Clone — Architecture

**Generated from:** `replica/recon.md` + `replica/features.csv`
**Target:** Android (Kotlin/Compose) + optional backend for sync/licensing
**Existing project:** `/root/maya_clone_project/` (51 Compose screens, Navigation, Theme)

---

## Stack Choices

| Layer | Choice | Why |
|-------|--------|-----|
| **Mobile** | Kotlin + Jetpack Compose (existing) | Already built, 51 screens, Material3, dark theme |
| **Local DB** | Room (SQLite) | Native Android, offline-first, compile-time SQL checks |
| **Sync/Backend** | Supabase (Postgres + Auth + Storage + Realtime) | Managed, cheap, row-level security, Dart/JS/Kotlin clients |
| **Auth** | Supabase Auth (email/magic link + Google) | No password storage, works offline with cached session |
| **Payments** | Google Play Billing (subscriptions) + Supabase for receipt verification | Platform-native, required for Play Store |
| **Push** | Firebase Cloud Messaging (FCM) | Only option for Android push |
| **Files** | Supabase Storage (encrypted) | Backup files, face embeddings, documents |
| **Voice (STT/TTS)** | On-device: ML Kit / SpeechRecognizer + system TTS; Cloud fallback: ElevenLabs / Google Cloud | Privacy-first, works offline |
| **Face Recognition** | ML Kit Face Detection + custom embedding (TensorFlow Lite) | On-device, no cloud dependency |
| **OCR** | ML Kit Text Recognition | On-device, no cloud |
| **Background Jobs** | WorkManager (local) + Supabase Edge Functions (server) | Battery-aware, survives process death |
| **CI/CD** | GitHub Actions → Play Store Internal Testing | Standard, free |

---

## Database Schema (Room + Supabase parity)

### 1. Users & Auth (Supabase Auth — not in Room)

```sql
-- Supabase auth.users is the source of truth
-- Room caches only: user_id, email, tier, avatar_url, updated_at
```

### 2. Agents

```sql
CREATE TABLE agents (
  id TEXT PRIMARY KEY,                    -- UUID v4
  user_id TEXT NOT NULL,                  -- FK to auth.users
  name TEXT NOT NULL,
  avatar_url TEXT,
  system_prompt TEXT NOT NULL DEFAULT '',
  model TEXT NOT NULL DEFAULT 'gpt-4o-mini',
  temperature REAL NOT NULL DEFAULT 0.7,
  tools_json TEXT NOT NULL DEFAULT '[]',  -- JSON array of tool IDs
  permissions_json TEXT NOT NULL DEFAULT '{}', -- JSON object
  is_active INTEGER NOT NULL DEFAULT 1,   -- 0/1
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_agents_user ON agents(user_id);
CREATE INDEX idx_agents_active ON agents(user_id, is_active);
```

### 3. Conversations

```sql
CREATE TABLE conversations (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  agent_id TEXT NOT NULL REFERENCES agents(id) ON DELETE CASCADE,
  title TEXT,
  message_count INTEGER NOT NULL DEFAULT 0,
  last_message_at INTEGER,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_conversations_user ON conversations(user_id);
CREATE INDEX idx_conversations_agent ON conversations(agent_id);
CREATE INDEX idx_conversations_recent ON conversations(user_id, last_message_at DESC);
```

### 4. Messages

```sql
CREATE TABLE messages (
  id TEXT PRIMARY KEY,
  conversation_id TEXT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
  role TEXT NOT NULL CHECK (role IN ('user','assistant','system','tool')),
  content TEXT NOT NULL DEFAULT '',
  attachments_json TEXT NOT NULL DEFAULT '[]', -- [{type, url, mime, size}]
  metadata_json TEXT NOT NULL DEFAULT '{}',    -- {model, tokens, tool_calls, error}
  status TEXT NOT NULL DEFAULT 'sent' CHECK (status IN ('sending','sent','streaming','failed','cancelled')),
  token_count INTEGER DEFAULT 0,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_messages_conversation ON messages(conversation_id, created_at);
CREATE INDEX idx_messages_status ON messages(status);
```

### 5. API Keys (encrypted at rest)

```sql
CREATE TABLE api_keys (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  provider TEXT NOT NULL,           -- 'openai', 'anthropic', 'elevenlabs', 'google', etc.
  key_hash TEXT NOT NULL,           -- Argon2id hash of the key (for verification)
  key_encrypted TEXT NOT NULL,      -- AES-256-GCM encrypted key (decrypted in memory only)
  label TEXT,
  scopes_json TEXT NOT NULL DEFAULT '[]',
  last_used_at INTEGER,
  expires_at INTEGER,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_api_keys_user ON api_keys(user_id);
CREATE INDEX idx_api_keys_provider ON api_keys(user_id, provider);
```

### 6. Face Profiles

```sql
CREATE TABLE face_profiles (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  name TEXT NOT NULL,
  embedding BLOB NOT NULL,          -- 512-byte float32 array (ML Kit / custom)
  photo_urls_json TEXT NOT NULL DEFAULT '[]', -- Supabase Storage URLs
  quality_score REAL,               -- 0.0-1.0 enrollment quality
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_face_profiles_user ON face_profiles(user_id);
```

### 7. Face Lock Attempts

```sql
CREATE TABLE face_lock_attempts (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  success INTEGER NOT NULL,         -- 0/1
  confidence REAL,                  -- 0.0-1.0
  fallback_used INTEGER NOT NULL DEFAULT 0, -- 0/1 (PIN/pattern)
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_face_attempts_user_time ON face_lock_attempts(user_id, created_at DESC);
```

### 8. Backup Jobs

```sql
CREATE TABLE backup_jobs (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  type TEXT NOT NULL CHECK (type IN ('full','incremental','settings_only','faces_only')),
  status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','running','completed','failed','cancelled')),
  storage_path TEXT,                -- Supabase Storage path
  size_bytes INTEGER,
  error_message TEXT,
  started_at INTEGER,
  completed_at INTEGER,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_backup_jobs_user ON backup_jobs(user_id, created_at DESC);
```

### 9. Auto-Reply Rules

```sql
CREATE TABLE auto_reply_rules (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  name TEXT NOT NULL,
  conditions_json TEXT NOT NULL,    -- [{field, operator, value}]
  actions_json TEXT NOT NULL,       -- [{type, params}]
  priority INTEGER NOT NULL DEFAULT 0,
  enabled INTEGER NOT NULL DEFAULT 1,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_autoreply_user ON auto_reply_rules(user_id, enabled, priority DESC);
```

### 10. Awareness Events

```sql
CREATE TABLE awareness_events (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  type TEXT NOT NULL,               -- 'location', 'sensor', 'app', 'notification', 'call'
  payload_json TEXT NOT NULL,       -- typed per event type
  processed INTEGER NOT NULL DEFAULT 0,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_awareness_user_time ON awareness_events(user_id, created_at DESC);
CREATE INDEX idx_awareness_type ON awareness_events(user_id, type);
```

### 11. Triggers (Automation)

```sql
CREATE TABLE triggers (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  name TEXT NOT NULL,
  event_type TEXT NOT NULL,         -- matches awareness_events.type
  conditions_json TEXT NOT NULL,    -- evaluated against event payload
  actions_json TEXT NOT NULL,       -- [{type, params}]
  enabled INTEGER NOT NULL DEFAULT 1,
  cooldown_ms INTEGER DEFAULT 0,    -- debounce
  last_fired_at INTEGER,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_triggers_user ON triggers(user_id, enabled);
```

### 12. Encryption Keys

```sql
CREATE TABLE encryption_keys (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  purpose TEXT NOT NULL CHECK (purpose IN ('backup','face_embeddings','api_keys','messages','export')),
  algorithm TEXT NOT NULL DEFAULT 'AES-256-GCM',
  key_material_encrypted TEXT NOT NULL, -- wrapped by master key (Keystore/StrongBox)
  key_version INTEGER NOT NULL DEFAULT 1,
  rotated_at INTEGER,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE UNIQUE INDEX uq_encryption_key_user_purpose ON encryption_keys(user_id, purpose, key_version);
```

### 13. Connectors (OAuth tokens)

```sql
CREATE TABLE connectors (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  type TEXT NOT NULL,               -- 'google', 'whatsapp', 'gmail', 'calendar', 'notion', etc.
  config_json TEXT NOT NULL,        -- provider-specific config
  access_token_encrypted TEXT,      -- encrypted OAuth token
  refresh_token_encrypted TEXT,
  token_expires_at INTEGER,
  status TEXT NOT NULL DEFAULT 'disconnected' CHECK (status IN ('connected','disconnected','error','expired')),
  last_sync_at INTEGER,
  error_message TEXT,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_connectors_user ON connectors(user_id, status);
```

### 14. Themes (user custom themes)

```sql
CREATE TABLE themes (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  name TEXT NOT NULL,
  is_custom INTEGER NOT NULL DEFAULT 1,
  colors_json TEXT NOT NULL,        -- full Material3 color scheme
  typography_json TEXT NOT NULL,
  spacing_json TEXT NOT NULL,
  shape_json TEXT NOT NULL,
  is_active INTEGER NOT NULL DEFAULT 0,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_themes_user ON themes(user_id, is_active);
```

### 15. Licenses (local cache of Play receipt)

```sql
CREATE TABLE licenses (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  tier TEXT NOT NULL CHECK (tier IN ('free','pro','promax')),
  purchase_token TEXT,              -- Play Billing purchase token
  expires_at INTEGER,               -- null = lifetime
  features_json TEXT NOT NULL DEFAULT '[]',
  verified_at INTEGER,
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000)
);
CREATE INDEX idx_licenses_user ON licenses(user_id);
```

---

## Row-Level Security (Supabase)

```sql
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
```

---

## API Routes (Supabase Edge Functions + Client SDK)

| Method | Path | Purpose | Auth | Flow |
|--------|------|---------|------|------|
| POST | `/functions/v1/chat/completions` | Proxy LLM calls (hide API keys) | User JWT | F01, F06 |
| POST | `/functions/v1/voice/stt` | Speech-to-text (cloud fallback) | User JWT | F06 |
| POST | `/functions/v1/voice/tts` | Text-to-speech (cloud fallback) | User JWT | F06 |
| POST | `/functions/v1/face/verify` | Face verification (server-side backup) | User JWT | F04 |
| POST | `/functions/v1/backup/create` | Initiate encrypted backup upload | User JWT | F05 |
| POST | `/functions/v1/backup/restore` | Download & decrypt backup | User JWT | F05 |
| POST | `/functions/v1/license/verify` | Verify Play receipt with Google | User JWT | F10 |
| POST | `/functions/v1/webhook/stripe` | Stripe webhook (if used for web) | Stripe sig | — |
| POST | `/functions/v1/webhook/fcm` | FCM upstream (optional) | FCM auth | — |
| GET | `/functions/v1/sync/pull` | Pull remote changes (since timestamp) | User JWT | F05 |
| POST | `/functions/v1/sync/push` | Push local changes | User JWT | F05 |

**Webhooks In:**
- Google Play RTDN (Real-time Developer Notifications) → `/functions/v1/license/verify`
- FCM → handled by client SDK

**Background Jobs (Edge Functions + pg_cron):**
- `0 3 * * *` — Daily cleanup: expired face attempts, old awareness events (>90d)
- `0 4 * * 0` — Weekly backup reminder notification
- `*/15 * * * *` — Process pending auto-reply rules
- `0 * * * *` — Sync connector data (email, calendar)

---

## Hard Parts That Bite

| Area | Risk | Mitigation |
|------|------|------------|
| **Time zones** | Awareness events, scheduling, backups | Store all timestamps as UTC milliseconds (INTEGER). Convert to user TZ only in UI. |
| **Idempotency** | Backup uploads, license verification, webhook retries | Every mutating API accepts `idempotency_key`. Dedupe in Edge Function via Redis/Postgres. |
| **Race conditions** | Concurrent message inserts, face enrollment | Room: `@Insert(onConflict = REPLACE)`. Supabase: unique constraints + `ON CONFLICT`. |
| **Rate limits** | LLM APIs, FCM, Play Billing | Token bucket per user in Edge Function. Exponential backoff + retry queue in WorkManager. |
| **File size limits** | Backup files, face photos, documents | Chunked upload to Supabase Storage (5MB parts). Resume on failure. |
| **Realtime voice** | STT/TTS latency, offline | On-device first (ML Kit, SpeechRecognizer). Cloud only on explicit fallback. |
| **Offline-first** | All features must work without network | Room is source of truth. Supabase sync is eventual. Conflict resolution: last-write-wins per entity + server timestamp. |
| **Email deliverability** | Not used (no email auth) | N/A — magic link via Supabase if needed later. |
| **Multi-tenancy** | Single-user app | `user_id` = `auth.uid()` everywhere. No org/workspace concept. |
| **GDPR deletion** | User requests data wipe | `DELETE FROM ... WHERE user_id = ?` cascade. Supabase Auth `DELETE /admin/users/:id`. |

---

## Build Order (Milestones)

### M0: Vertical Slice (Core Loop) — **Week 1-2**
**Screens:** S01 Home, S02 Chat, S04 Settings, S03 AgentPicker
**Tables:** agents, conversations, messages, api_keys
**Routes:** `chat/completions`, `voice/stt`, `voice/tts`
**Goal:** Sign up → create agent → chat with voice → see history

| Task | Done? |
|------|-------|
| Room schema + DAOs for 4 tables | ☐ |
| Supabase project + Auth + RLS | ☐ |
| HomeScreen → AgentPicker → Chat flow | ☐ |
| ChatScreen: streaming, voice button | ☐ |
| SettingsSecretScreen: API key CRUD (encrypted) | ☐ |
| Edge Function: chat proxy (hides keys) | ☐ |
| On-device STT/TTS integration | ☐ |

---

### M1: Must-Haves (Settings & Security) — **Week 3-4**
**Screens:** S05-S32 (28 settings screens), S33 AwarenessLog, S34 EventTriggers, S35 KeySetup, S36 Permissions, S37 Advanced
**Tables:** face_profiles, face_lock_attempts, backup_jobs, auto_reply_rules, awareness_events, triggers, encryption_keys, themes, licenses
**Routes:** `face/verify`, `backup/create`, `backup/restore`, `license/verify`, `sync/pull`, `sync/push`
**Goal:** Full settings, face lock, backup/restore, licensing, automation

| Task | Done? |
|------|-------|
| All 28 Settings sub-screens wired to Room | ☐ |
| Face enrollment (ML Kit) + lock screen | ☐ |
| Backup: encrypt → chunk → upload → restore | ☐ |
| Play Billing integration + license cache | ☐ |
| Auto-reply engine (WorkManager) | ☐ |
| Awareness logging (location/sensors) | ☐ |
| Trigger engine (evaluate on event) | ☐ |
| Key rotation (Keystore/StrongBox) | ☐ |

---

### M2: Should-Haves (Integrations & Media) — **Week 5-6**
**Screens:** S10 Email, S11 WhatsApp, S16 Connectors, S38 CodeProjects, S39 Documents, S40 MyPictures, S42 Yaadein, S43 PhotosOf, S50 Scanner
**Tables:** connectors
**Routes:** OAuth callbacks, connector sync
**Goal:** Email, WhatsApp (stub), connectors, docs, photos, memories, scanner

| Task | Done? |
|------|-------|
| OAuth flows for Google, WhatsApp (QR) | ☐ |
| Connector framework (token refresh, sync) | ☐ |
| Document viewer (PDF, MD, txt) | ☐ |
| Photo gallery (MediaStore) + face grouping | ☐ |
| Yaadein timeline (awareness + chat + photos) | ☐ |
| Scanner: ML Kit OCR + crop → save | ☐ |

---

### M3: Could-Haves (Advanced/Community) — **Week 7-8**
**Screens:** S44 Notices, S45 Portal, S46 Markets, S47 MayaRules, S48 Whiteboard, S49 Vibes, S51 Marketing, S52 Privacy, S53 About, S54 License, S55 TLS, S56 Blocked
**Routes:** WebRTC signaling (Portal), Market data webhook
**Goal:** Polish, collab, marketplace, compliance

| Task | Done? |
|------|-------|
| Portal: WebRTC data channel (peer-to-peer) | ☐ |
| Whiteboard: shared canvas (Yjs + WebRTC) | ☐ |
| Markets: data provider integration | ☐ |
| MayaRules: visual rule builder | ☐ |
| Vibes: ambient sound scheduler | ☐ |
| TLS cert pinning management | ☐ |
| Blocked contacts (CallLog/ContactsProvider) | ☐ |

---

## Summary

- **Stack:** Kotlin/Compose + Room + Supabase (Postgres/Auth/Storage/Realtime/Edge) + ML Kit + Play Billing + FCM
- **Tables:** 15 (plus Supabase auth.users)
- **Routes:** 11 Edge Functions + 3 webhooks + 4 cron jobs
- **Riskiest:** Offline-first sync conflict resolution, Face recognition accuracy, Realtime voice latency, Play Billing edge cases
- **Next:** `/replica-design` — rebuild design system from existing Theme.kt/Color.kt as tokens + component specs