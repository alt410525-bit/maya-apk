# Maya AI Assistant — Recon Map

**Source app:** Maya AI Assistant (com.getmaya.android)
**Platform:** Android
**Clone package:** com.maya.clone
**Scope:** Full app — core AI chat, voice, settings (28 sub-screens), sub-agents, skills, connectors, awareness, face recognition, backup, themes, and all subsystems

---

## Sources Table

| Source | What it gives |
|--------|--------------|
| Existing clone project (`/root/maya_clone_project/`) | Complete screen inventory (51 screens), navigation map, component list, theme system |
| Original APKs in `/sdcard/maya4/` | Reference for behavior verification (not decompiled) |
| User's own Maya account | Real flows, states, data |

---

## Screen Inventory (51 screens)

| ID | Screen | Route | Purpose | Key Components | States |
|----|--------|-------|---------|----------------|--------|
| S01 | HomeScreen | `maya_home` | Main dashboard, agent picker entry | Agent cards, quick actions, status chips | Empty (no agents), loading, filled |
| S02 | ChatScreen | `chat` | AI conversation with voice | Message list, input bar, voice button, model selector | Empty, streaming, error, voice active |
| S03 | AgentPickerScreen | `agent_picker` | Select/create sub-agents | Grid/list, create dialog, search | Empty, loading, filled |
| S04 | SettingsScreen | `settings` | Root settings hub | Sectioned list, navigation | Filled |
| S05 | SettingsPersonalScreen | `settings_personal` | Profile, name, avatar | Input fields, image picker | Filled, editing |
| S06 | SettingsMayaScreen | `settings_maya` | Core AI behavior config | Toggles, sliders, model selection | Filled |
| S07 | SettingsSecretScreen | `settings_secret` | API keys, secrets management | Masked inputs, reveal toggle, validation | Filled, editing, error |
| S08 | SettingsSubagentsScreen | `settings_subagents` | Manage sub-agents | List, create/edit/delete, permissions | Empty, filled |
| S09 | SettingsSkillsScreen | `settings_skills` | Skill marketplace/install | Grid, categories, install status | Loading, filled, installing |
| S10 | SettingsEmailScreen | `settings_email` | Email integration | OAuth, sync settings, test send | Connected, disconnected, error |
| S11 | SettingsWhatsAppGroupsScreen | `settings_whatsapp_groups` | WhatsApp group linking | QR pair, group list, permissions | Paired, unpaired, syncing |
| S12 | SettingsOptionalScreen | `settings_optional` | Optional feature toggles | Toggle grid, descriptions | Filled |
| S13 | SettingsBatteryScreen | `settings_battery` | Battery optimization | Whitelist status, instructions | Optimized, not optimized |
| S14 | SettingsVoiceEngineScreen | `settings_voice_engine` | TTS/STT engine selection | Radio group, test playback, download | Selected, downloading, error |
| S15 | SettingsSocialScreen | `settings_social` | Social sharing, invites | Share sheet, referral code, links | Filled |
| S16 | SettingsConnectorsScreen | `settings_connectors` | Third-party integrations | Card list, OAuth flows, status | Connected, disconnected, error |
| S17 | SettingsBackupScreen | `settings_backup` | Data backup/restore | Local/cloud, schedule, encrypt | Idle, backing up, restoring |
| S18 | SettingsThemeScreen | `settings_theme` | Dark/light/system theme | Radio, preview, custom colors | Filled |
| S19 | SettingsAppearanceScreen | `settings_appearance` | Font, density, animations | Sliders, pickers, live preview | Filled |
| S20 | SettingsBehaviourScreen | `settings_behaviour` | App behavior toggles | Toggle list, descriptions | Filled |
| S21 | SettingsTypingScreen | `settings_typing` | Typing indicators, sounds | Toggles, volume, preview | Filled |
| S22 | SettingsGuardianScreen | `settings_guardian` | Safety/moderation controls | Sensitivity, blocked words, reports | Filled, editing |
| S23 | SettingsFacesScreen | `settings_faces` | Face recognition management | List, add, delete, train | Empty, training, filled |
| S24 | SettingsFacesCaptureScreen | `settings_faces_capture` | Camera capture for faces | Camera preview, guide overlay, confirm | Permission, capturing, preview |
| S25 | SettingsFaceLockScreen | `settings_face_lock` | App lock with face | Toggle, fallback PIN, timeout | Locked, unlocked, failed |
| S26 | SettingsFaceAttemptsScreen | `settings_face_attempts` | Failed attempt log | List, timestamps, clear | Empty, filled |
| S27 | SettingsSOSScreen | `settings_sos` | Emergency contacts/alerts | Contact picker, message template, test | Configured, not configured |
| S28 | SettingsTouchGuardScreen | `settings_touch_guard` | Accidental touch prevention | Sensitivity, zones, test | Filled |
| S29 | SettingsAwarenessScreen | `settings_awareness` | Context awareness config | Location, sensors, triggers | Enabled, disabled, error |
| S30 | SettingsAutoReplyScreen | `settings_autoreply` | Auto-reply rules | Rule builder, conditions, actions | Empty, filled, editing |
| S31 | SettingsScreenLockScreen | `settings_screen_lock` | Screen lock type | PIN, pattern, biometric, none | Set, not set |
| S32 | SettingsAccessibilityScreen | `settings_accessibility` | A11y services, shortcuts | Service list, enable toggles | Enabled, disabled |
| S33 | AwarenessLogScreen | `awareness_log` | Context event timeline | List, filter, detail view | Empty, filled |
| S34 | EventTriggersScreen | `event_triggers` | Automation triggers | List, create/edit, test | Empty, filled |
| S35 | KeySetupScreen | `key_setup` | Encryption key management | Generate, import, export, rotate | Set, not set, rotating |
| S36 | PermissionsScreen | `permissions` | Runtime permissions | Grid, status, request rationale | Granted, denied, rationale |
| S37 | AdvancedScreen | `advanced` | Developer/debug options | Logs, crash reports, flags | Filled |
| S38 | CodeProjectsScreen | `code_projects` | Code project management | List, create, sync, deploy | Empty, filled |
| S39 | DocumentsScreen | `documents` | Document viewer/editor | List, search, open, share | Empty, filled |
| S40 | MyPicturesScreen | `my_pictures` | Personal photo gallery | Grid, albums, share, delete | Empty, filled |
| S41 | SettingsFacesCaptureScreen | `settings_faces_capture` | (Duplicate entry — see S24) | — | — |
| S42 | YaadeinScreen | `yaadein` | Memories/timeline view | Timeline, filter, search | Empty, filled |
| S43 | PhotosOfScreen | `photos_of` | Face-grouped photos | Person chips, grid, merge | Empty, filled |
| S44 | NoticesScreen | `notices` | System notifications | List, dismiss, action | Empty, filled |
| S45 | PortalScreen | `portal` | Remote access/control | QR, session list, revoke | Active, inactive |
| S46 | MarketsScreen | `markets` | Market data/monitoring | Charts, watchlist, alerts | Empty, filled |
| S47 | MayaRulesScreen | `maya_rules` | Custom automation rules | Rule editor, conditions, actions | Empty, filled |
| S48 | WhiteboardScreen | `whiteboard` | Collaborative canvas | Toolbar, layers, export | Empty, drawing |
| S49 | VibesScreen | `vibes` | Mood/ambience settings | Presets, custom, scheduler | Filled |
| S50 | ScannerScreen | `scanner` | Document/QR scanner | Camera, crop, OCR, save | Scanning, preview, saved |
| S51 | MarketingScreen | `marketing` | Promo/referral screen | Carousel, copy, share | Filled |
| S52 | PrivacyScreen | `privacy` | Privacy policy, data rights | WebView, export, delete | Loaded |
| S53 | AboutScreen | `about` | Version, credits, licenses | Text, links, copy version | Filled |
| S54 | LicenseScreen | `license` | License management | Status, upgrade, restore | Free, Pro, ProMax |
| S55 | TLSCertScreen | `tls_cert` | Certificate pinning/management | List, add, verify, delete | Empty, filled |
| S56 | BlockedScreen | `blocked` | Blocked contacts/numbers | List, add, remove, import | Empty, filled |

---

## User Flows

| ID | Flow | Screens | Edge Cases |
|----|------|---------|------------|
| F01 | First launch → Home → Chat | S01 → S02 | No agents created, mic permission denied |
| F02 | Create sub-agent | S01 → S03 → create → S01 | Name conflict, quota exceeded |
| F03 | Configure API keys | S04 → S07 | Invalid key, network error, rate limit |
| F04 | Enable face lock | S04 → S25 → S24 → S25 | Camera permission, enrollment failed, fallback PIN |
| F05 | Backup & restore | S04 → S17 → backup → restore | Storage full, corrupt backup, version mismatch |
| F06 | Voice chat | S02 → voice button → speak → response | STT timeout, TTS download, no network |
| F07 | WhatsApp group linking | S04 → S11 → QR scan → paired | QR expired, group left, re-pair |
| F08 | Auto-reply rule | S04 → S30 → create rule → test | Condition never matches, action fails |
| F09 | Theme customization | S04 → S18 → S19 → preview → apply | Contrast fail, font not found |
| F10 | License upgrade | S04 → S54 → purchase → restore | Purchase failed, restore failed, platform mismatch |

---

## Component Inventory

| Component | Variants | States | Screens Used |
|-----------|----------|--------|--------------|
| BackTopAppBar | Title + back, Title + actions, Search | Default, scrolled | All screens |
| AgentCard | Default, selected, loading | Default, pressed, loading | S01, S03 |
| MessageBubble | User, Assistant, System, Error | Default, streaming, failed | S02 |
| VoiceButton | Idle, listening, processing, speaking | Default, active, disabled | S02 |
| SettingsSection | Header, divider, description | Default | S04–S32 |
| ToggleRow | Switch, checkbox, radio | On, off, disabled | S04–S32 |
| InputRow | Text, password, number, email | Default, focused, error | S05, S07, S24 |
| Chip | Filter, action, status | Default, selected, disabled | S01, S03, S09 |
| EmptyState | Illustration, title, action | Default | S01, S03, S09, S33 |
| LoadingOverlay | Spinner, progress, skeleton | Active | All async screens |
| BottomSheet | Modal, persistent, drag handle | Open, closed, half | S03, S24, S30 |
| Dialog | Alert, confirm, input, list | Open, closing | S07, S25, S35 |
| Card | Elevated, outlined, filled | Default, pressed | S09, S11, S16 |
| NavigationRail | 3–5 destinations, badge | Selected, unselected | S01 (tablet) |

---

## Inferred Data Model

| Entity | Fields | Evidence | Confidence |
|--------|--------|----------|------------|
| Agent | id, name, avatar, systemPrompt, model, temperature, tools[], permissions[], createdAt | S01, S03, S08 | High |
| Conversation | id, agentId, messages[], startedAt, updatedAt | S02 | High |
| Message | id, conversationId, role, content, attachments[], metadata, timestamp | S02 | High |
| ApiKey | id, provider, keyHash, label, scopes[], lastUsed, expiresAt | S07 | High |
| FaceProfile | id, name, embeddings[], photos[], createdAt, updatedAt | S23, S24, S43 | High |
| BackupJob | id, type, status, path, size, startedAt, completedAt, error | S17 | Medium |
| AutoReplyRule | id, name, conditions[], actions[], enabled, priority | S30 | Medium |
| License | tier (free/pro/promax), expiresAt, features[], receiptToken | S54 | High |
| AwarenessEvent | id, type, timestamp, location, sensorData, processed | S33 | Medium |
| Trigger | id, name, eventType, conditions[], actions[], enabled | S34 | Medium |
| EncryptionKey | id, algorithm, keyMaterial, createdAt, rotatedAt, purpose | S35 | High |
| Connector | id, type, config, status, lastSync, error | S16 | Medium |
| Theme | id, name, colors{}, typography{}, spacing{}, isCustom | S18, S19 | High |

---

## Feature Matrix (replica/features.csv)

See `replica/features.csv` for full matrix with priority (must/should/could) and clone status.

---

## What Cannot Be Cloned (skip rows)

| Feature | Reason |
|---------|--------|
| Maya's proprietary model weights | Licensed IP, not user's to clone |
| Maya's cloud inference infrastructure | Owned by original vendor |
| Maya's user accounts & sync backend | Private user data, vendor-owned |
| WhatsApp Business API integration | Requires Meta partnership |
| Google Play licensing / in-app billing | Platform-specific, vendor account |
| Face recognition model (if proprietary) | May be vendor IP — use ML Kit / local alternative |
| Push notification infrastructure (FCM) | Requires vendor's Firebase project |

---

## Size Estimate

| Metric | Count |
|--------|-------|
| Screens | 56 |
| Flows | 10 |
| Entities | 13 |
| Components | 14 |
| Hard parts | Realtime voice (STT/TTS), Face recognition, Offline sync, Encryption, Sub-agent sandboxing |
| Estimate | **L (a quarter)** for full parity; **M (few weeks)** for core loop (chat + settings + agents) |

---

## Next Step

Run `/replica-architect` to plan the stack, database schema, and API from this recon map.