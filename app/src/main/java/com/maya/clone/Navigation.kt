package com.maya.clone

/**
 * All 55 app routes extracted from CA0.java navigation host.
 * These are the unique screen identifiers used throughout the app.
 * This object is imported as Navigation throughout the project.
 */
object Navigation {
    // === Main screens ===
    const val HOME = "home"
    const val CHAT = "chat"
    const val MAYA_HOME = "maya_home"
    const val SETTINGS = "settings"

    // === Settings sub-screens ===
    const val SETTINGS_PERSONAL = "settings_personal"
    const val SETTINGS_MAYA = "settings_maya"
    const val SETTINGS_SECRET = "settings_secret"
    const val SETTINGS_SUBAGENTS = "settings_subagents"
    const val SETTINGS_SKILLS = "settings_skills"
    const val SETTINGS_EMAIL = "settings_email"
    const val SETTINGS_WHATSAPP_GROUPS = "settings_whatsapp_groups"
    const val SETTINGS_OPTIONAL = "settings_optional"
    const val SETTINGS_BATTERY = "settings_battery"
    const val SETTINGS_VOICE_ENGINE = "settings_voice_engine"
    const val SETTINGS_SOCIAL = "settings_social"
    const val SETTINGS_CONNECTORS = "settings_connectors"
    const val SETTINGS_BACKUP = "settings_backup"
    const val SETTINGS_THEME = "settings_theme"
    const val SETTINGS_APPEARANCE = "settings_appearance"
    const val SETTINGS_BEHAVIOUR = "settings_behaviour"
    const val SETTINGS_TYPING = "settings_typing"
    const val SETTINGS_GUARDIAN = "settings_guardian"
    const val SETTINGS_FACES = "settings_faces"
    const val SETTINGS_FACES_CAPTURE = "settings_faces_capture"
    const val SETTINGS_FACE_LOCK = "settings_face_lock"
    const val SETTINGS_FACE_ATTEMPTS = "settings_face_attempts"
    const val SETTINGS_SOS = "settings_sos"
    const val SETTINGS_TOUCH_GUARD = "settings_touch_guard"
    const val SETTINGS_AWARENESS = "settings_awareness"
    const val SETTINGS_AUTOREPLY = "settings_autoreply"
    const val SETTINGS_SCREEN_LOCK = "settings_screen_lock"
    const val SETTINGS_LOCK_CREDENTIALS = "settings_lock_credentials"
    const val SETTINGS_ACCESSIBILITY = "settings_accessibility"

    // === Feature screens ===
    const val AWARENESS_LOG = "awareness_log"
    const val EVENT_TRIGGERS = "event_triggers"
    const val KEY_SETUP = "key_setup"
    const val PERMISSIONS = "permissions"
    const val ADVANCED = "advanced"
    const val CODE_PROJECTS = "code_projects"
    const val DOCUMENTS = "documents"
    const val MY_PICTURES = "my_pictures"
    const val YAADEIN = "yaadein"
    const val PHOTOS_OF = "photos_of"
    const val NOTICES = "notices"
    const val PORTAL = "portal"
    const val MARKETS = "markets"
    const val MAYA_RULES = "maya_rules"
    const val WHITEBOARD = "whiteboard"
    const val VIBES = "vibes"
    const val SCANNER = "scanner"
    const val MARKETING = "marketing"
    const val PRIVACY = "privacy"
    const val ABOUT = "about"
    const val LICENSE = "license"
    const val TLS_CERT = "tls_cert"
    const val BLOCKED = "blocked"
}