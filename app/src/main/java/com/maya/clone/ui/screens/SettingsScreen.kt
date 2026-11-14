@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.SettingsViewModel

data class SettingsItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val route: String,
    val badge: String? = null
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    var selectedSection by remember { mutableStateOf<String?>(null) }
    val userId by remember { mutableStateOf("default_user") }
    val isLoading by settingsViewModel.isLoading.collectAsStateWithLifecycle()
    val error by settingsViewModel.error.collectAsStateWithLifecycle()
    val activeTheme by settingsViewModel.activeTheme.collectAsStateWithLifecycle()
    val latestLicense by settingsViewModel.latestLicense.collectAsStateWithLifecycle()
    val faceProfiles by settingsViewModel.faceProfiles.collectAsStateWithLifecycle()
    val backupJobs by settingsViewModel.backupJobs.collectAsStateWithLifecycle()
    val connectedConnectors by settingsViewModel.connectedConnectors.collectAsStateWithLifecycle()

    // Derived state
    val hasActiveLicense = latestLicense?.isValid == true
    val faceProfilesCount = faceProfiles.size
    val backupJobsCount = backupJobs.size
    val connectedConnectorsCount = connectedConnectors.size
    val activeThemeName = activeTheme?.name

    // Initialize the ViewModel with user ID
    LaunchedEffect(userId) {
        settingsViewModel.init(userId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // ── Top bar ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(CardDark, CircleShape)) {
                Icon(Icons.Default.ArrowBack, tint = TextSecondary, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Settings", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = DividerColor)

        // ── Settings list ────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                SectionHeader("Maya")
                SettingsSectionHeader("Maya Character")
            }
            item {
                SettingsRow(SettingsItem("Appearance", activeThemeName ?: "Orb, character, dark/light", Icons.Default.Style, Navigation.SETTINGS_APPEARANCE)) { onNavigate(Navigation.SETTINGS_APPEARANCE) }
                SettingsRow(SettingsItem("Voice & Audio", "Voice model, typing sound", Icons.Default.Mic, Navigation.SETTINGS_VOICE_ENGINE)) { onNavigate(Navigation.SETTINGS_VOICE_ENGINE) }
                SettingsRow(SettingsItem("Behaviour", "Startup, typing, echo guard", Icons.Default.Tune, Navigation.SETTINGS_BEHAVIOUR)) { onNavigate(Navigation.SETTINGS_BEHAVIOUR) }
                SettingsRow(SettingsItem("Touch Guard", "Prevent accidental taps", Icons.Default.TouchApp, Navigation.SETTINGS_TOUCH_GUARD)) { onNavigate(Navigation.SETTINGS_TOUCH_GUARD) }
            }

            item { Spacer(modifier = Modifier.height(12.dp)); SectionHeader("Personalisation") }
            item {
                SettingsRow(SettingsItem("Personal Info", "Name, age, preferences", Icons.Default.Person, Navigation.SETTINGS_PERSONAL)) { onNavigate(Navigation.SETTINGS_PERSONAL) }
                SettingsRow(SettingsItem("Maya Rules", "Automation & triggers", Icons.Default.AutoAwesome, Navigation.MAYA_RULES)) { onNavigate(Navigation.MAYA_RULES) }
                SettingsRow(SettingsItem("Sub-Agents", "Expert personas", Icons.Default.Hub, Navigation.SETTINGS_SUBAGENTS)) { onNavigate(Navigation.SETTINGS_SUBAGENTS) }
                SettingsRow(SettingsItem("Skills", "Custom capabilities", Icons.Default.Stars, Navigation.SETTINGS_SKILLS)) { onNavigate(Navigation.SETTINGS_SKILLS) }
                SettingsRow(SettingsItem("Vibes", "Mood & reactions", Icons.Default.Mood, Navigation.VIBES)) { onNavigate(Navigation.VIBES) }
            }

            item { Spacer(modifier = Modifier.height(12.dp)); SectionHeader("Connect") }
            item {
                SettingsRow(SettingsItem("Email & Alerts", null, Icons.Default.Email, Navigation.SETTINGS_EMAIL)) { onNavigate(Navigation.SETTINGS_EMAIL) }
                SettingsRow(SettingsItem("WhatsApp Groups", null, Icons.Default.Group, Navigation.SETTINGS_WHATSAPP_GROUPS)) { onNavigate(Navigation.SETTINGS_WHATSAPP_GROUPS) }
                SettingsRow(SettingsItem("Social & Calls", null, Icons.Default.Phone, Navigation.SETTINGS_SOCIAL)) { onNavigate(Navigation.SETTINGS_SOCIAL) }
                SettingsRow(SettingsItem("Connectors", if (connectedConnectorsCount > 0) "$connectedConnectorsCount connected" else "API integrations", Icons.Default.Link, Navigation.SETTINGS_CONNECTORS)) { onNavigate(Navigation.SETTINGS_CONNECTORS) }
                SettingsRow(SettingsItem("Advanced", "JARVIS Sites, PC control", Icons.Default.DeveloperMode, Navigation.ADVANCED)) { onNavigate(Navigation.ADVANCED) }
            }

            item { Spacer(modifier = Modifier.height(12.dp)); SectionHeader("Security") }
            item {
                SettingsRow(SettingsItem("Screen Lock", null, Icons.Default.Lock, Navigation.SETTINGS_SCREEN_LOCK)) { onNavigate(Navigation.SETTINGS_SCREEN_LOCK) }
                SettingsRow(SettingsItem("Face Lock", if (faceProfilesCount > 0) "$faceProfilesCount profiles" else null, Icons.Default.Face, Navigation.SETTINGS_FACE_LOCK)) { onNavigate(Navigation.SETTINGS_FACE_LOCK) }
                SettingsRow(SettingsItem("Guardian", "Parental controls", Icons.Default.Shield, Navigation.SETTINGS_GUARDIAN)) { onNavigate(Navigation.SETTINGS_GUARDIAN) }
                SettingsRow(SettingsItem("SOS", "Emergency contacts", Icons.Default.Warning, Navigation.SETTINGS_SOS)) { onNavigate(Navigation.SETTINGS_SOS) }
            }

            item { Spacer(modifier = Modifier.height(12.dp)); SectionHeader("System") }
            item {
                SettingsRow(SettingsItem("Accessibility", "Tap/click setup", Icons.Default.Accessibility, Navigation.SETTINGS_ACCESSIBILITY)) { onNavigate(Navigation.SETTINGS_ACCESSIBILITY) }
                SettingsRow(SettingsItem("Permissions", null, Icons.Default.Security, Navigation.PERMISSIONS)) { onNavigate(Navigation.PERMISSIONS) }
                SettingsRow(SettingsItem("Backup & Sync", if (backupJobsCount > 0) "$backupJobsCount jobs" else null, Icons.Default.CloudSync, Navigation.SETTINGS_BACKUP)) { onNavigate(Navigation.SETTINGS_BACKUP) }
                SettingsRow(SettingsItem("Battery Optimisation", null, Icons.Default.BatteryChargingFull, Navigation.SETTINGS_BATTERY)) { onNavigate(Navigation.SETTINGS_BATTERY) }
                SettingsRow(SettingsItem("Awareness", "Screen context settings", Icons.Default.Visibility, Navigation.SETTINGS_AWARENESS)) { onNavigate(Navigation.SETTINGS_AWARENESS) }
                SettingsRow(SettingsItem("Event Triggers", null, Icons.Default.Bolt, Navigation.EVENT_TRIGGERS)) { onNavigate(Navigation.EVENT_TRIGGERS) }
                SettingsRow(SettingsItem("Optional Features", null, Icons.Default.ToggleOn, Navigation.SETTINGS_OPTIONAL)) { onNavigate(Navigation.SETTINGS_OPTIONAL) }
            }

            item { Spacer(modifier = Modifier.height(12.dp)); SectionHeader("About") }
            item {
                SettingsRow(SettingsItem("About Maya", "AI girlfriend on your phone", Icons.Default.Info, Navigation.ABOUT)) { onNavigate(Navigation.ABOUT) }
                SettingsRow(SettingsItem("Privacy Policy", null, Icons.Default.PrivacyTip, Navigation.PRIVACY)) { onNavigate(Navigation.PRIVACY) }
                SettingsRow(SettingsItem("Licences", if (hasActiveLicense) "Active" else "Open source", Icons.Default.Description, Navigation.LICENSE)) { onNavigate(Navigation.LICENSE) }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(title, color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp))
}

@Composable
private fun SettingsRow(item: SettingsItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).background(NeonPurple.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(item.icon, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = TextPrimary, fontSize = 14.sp)
            if (item.subtitle != null) {
                Text(item.subtitle, color = TextTertiary, fontSize = 11.sp)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
    }
}
