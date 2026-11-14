@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.SettingsOptionalViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsOptionalScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsOptionalViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val features by viewModel.features.collectAsStateWithLifecycle(mapOf())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Optional Features", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Features")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading features...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            val featureList = listOf(
                "voice_recognition" to "Voice Recognition" to "Speech-to-text and voice commands",
                "face_unlock" to "Face Unlock" to "Unlock with facial recognition",
                "auto_backup" to "Auto Backup" to "Automatic daily backups to cloud",
                "cloud_sync" to "Cloud Sync" to "Sync data across devices",
                "beta_features" to "Beta Features" to "Early access to experimental features",
                "analytics" to "Analytics" to "Help improve Maya with usage data",
                "crash_reporting" to "Crash Reporting" to "Automatically report crashes",
                "personalization" to "Personalization" to "Personalized responses and suggestions"
            )

            featureList.forEach { (id, title, description) ->
                val enabled = features[id] ?: false
                SettingsToggle(
                    label = title,
                    subtitle = description,
                    checked = enabled,
                    onCheckedChange = { viewModel.toggleFeature(id) }
                )
            }
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "About")

        SettingsRow(
            label = "Version",
            subtitle = "Maya Clone v2.0.0",
            trailing = { Text("2.0.0", color = TextSecondary, fontSize = 14.sp) }
        )

        SettingsRow(
            label = "Feedback",
            subtitle = "Send us your thoughts",
            trailing = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextTertiary) }
        )

        SettingsRow(
            label = "Report Issue",
            subtitle = "Report a bug or feature request",
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = NeonCyan) },
            onClick = { onNavigate(Navigation.SETTINGS_FEEDBACK) }
        )
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}