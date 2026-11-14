@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maya.clone.Navigation
import com.maya.clone.ui.components.BackTopAppBar
import com.maya.clone.ui.screens.*
import com.maya.clone.ui.theme.MayaCloneTheme
import com.maya.clone.ui.theme.DarkBackground

@Composable
fun MayaCloneApp() {
    MayaCloneTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
            val navController = rememberNavController()
            MayaNavHost(navController)
        }
    }
}

@Composable
private fun MayaNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Navigation.MAYA_HOME
    ) {
        composable(Navigation.MAYA_HOME) {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable("chat") {
            ChatScreen(onBack = { navController.popBackStack() }, conversationId = "default", userId = "default_user")
        }
        composable(Navigation.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // ── Real screens (from CA0 route map) ───────────────
        composable(Navigation.SETTINGS_PERSONAL) {
            SettingsPersonalScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_MAYA) {
            SettingsMayaScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_SECRET) {
            SettingsSecretScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_SUBAGENTS) {
            SettingsSubagentsScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_SKILLS) {
            SettingsSkillsScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_EMAIL) {
            SettingsEmailScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_WHATSAPP_GROUPS) {
            SettingsWhatsAppGroupsScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_OPTIONAL) {
            SettingsOptionalScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_BATTERY) {
            SettingsBatteryScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_VOICE_ENGINE) {
            SettingsVoiceEngineScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_SOCIAL) {
            SettingsSocialScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_CONNECTORS) {
            SettingsConnectorsScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_BACKUP) {
            SettingsBackupScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_THEME) {
            SettingsThemeScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_APPEARANCE) {
            SettingsAppearanceScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_BEHAVIOUR) {
            SettingsBehaviourScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_TYPING) {
            SettingsTypingScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_GUARDIAN) {
            SettingsGuardianScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_FACES) {
            SettingsFacesScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_FACES_CAPTURE) {
            SettingsFacesCaptureScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_FACE_LOCK) {
            SettingsFaceLockScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_FACE_ATTEMPTS) {
            SettingsFaceAttemptsScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_SOS) {
            SettingsSOSScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_TOUCH_GUARD) {
            SettingsTouchGuardScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_AWARENESS) {
            SettingsAwarenessScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_AUTOREPLY) {
            SettingsAutoReplyScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_SCREEN_LOCK) {
            SettingsScreenLockScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.SETTINGS_ACCESSIBILITY) {
            SettingsAccessibilityScreen(onBack = { navController.popBackStack() }, onNavigate = { navController.navigate(it) })
        }
        composable(Navigation.AWARENESS_LOG) {
            AwarenessLogScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.EVENT_TRIGGERS) {
            EventTriggersScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.KEY_SETUP) {
            KeySetupScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.PERMISSIONS) {
            PermissionsScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.ADVANCED) {
            AdvancedScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.CODE_PROJECTS) {
            CodeProjectsScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.DOCUMENTS) {
            DocumentsScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.MY_PICTURES) {
            MyPicturesScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.YAADEIN) {
            YaadeinScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.PHOTOS_OF) {
            PhotosOfScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.NOTICES) {
            NoticesScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.PORTAL) {
            PortalScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.MARKETS) {
            MarketsScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.MAYA_RULES) {
            MayaRulesScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.WHITEBOARD) {
            WhiteboardScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.VIBES) {
            VibesScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.SCANNER) {
            ScannerScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.MARKETING) {
            MarketingScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.PRIVACY) {
            PrivacyScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.LICENSE) {
            LicenseScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) })
        }
        composable(Navigation.TLS_CERT) {
            TLSCertScreen(onBack = { navController.popBackStack() })
        }
        composable(Navigation.BLOCKED) {
            BlockedScreen(onBack = { navController.popBackStack() })
        }
        // Agent picker at root level
        composable("agent_picker") {
            val userId = "default_user" // In a real app this would come from auth
            AgentPickerScreen(
                userId = userId,
                onSelect = { agentId -> /* handle */ },
                onBack = { navController.popBackStack() }
            )
        }
    }
}