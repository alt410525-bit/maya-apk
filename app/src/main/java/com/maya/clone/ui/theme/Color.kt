package com.maya.clone.ui.theme

import androidx.compose.ui.graphics.Color

// Primary brand — design tokens from replica/design/tokens.json
val NeonPurple = Color(0xFF7C4DFF)      // accent (darkened for AA contrast)
val NeonCyan = Color(0xFF22D3EE)        // accent-secondary
val NeonPink = Color(0xFFD81B60)        // accent-tertiary (darkened for AA contrast)

// Background / surface
val DarkBackground = Color(0xFF070710)  // bg
val DarkSurface = Color(0xFF0F0F1A)     // surface
val CardElevated = Color(0xFF16162A)    // surface-elevated
val CardDark = Color(0xFF1A1A2E)        // surface-card
val DividerColor = Color(0xFF6A6A9A)         // border (lightened for AA contrast)

// Text
val TextPrimary = Color(0xFFF8FAFC)     // text
val TextSecondary = Color(0xFF94A3B8)   // text-muted
val TextTertiary = Color(0xFF64748B)    // text-dim
val TextAccent = NeonCyan               // text-on-accent-secondary

// Status
val Success = Color(0xFF10B981)         // success
val Warning = Color(0xFFF59E0B)         // warning
val Error = Color(0xFFDC2626)           // danger (darkened for AA contrast)

// Feature category colours (for home screen icons)
val ChatColor = NeonCyan                // category-chat
val SettingsColor = NeonPurple          // category-settings
val FilesColor = Success                // category-files
val VoiceColor = NeonPink               // category-voice
val AwarenessColor = Warning            // category-awareness

// Additional tokens for components
val BorderInput = Color(0xFF7A7AAA)     // border-input
val FocusRing = NeonPurple              // focus-ring
val OverlayScrim = Color(0xCC000000)    // overlay-scrim (80% black)
val DisabledBg = CardDark.copy(alpha = 0.38f) // disabled-bg
val DisabledText = TextTertiary         // disabled-text