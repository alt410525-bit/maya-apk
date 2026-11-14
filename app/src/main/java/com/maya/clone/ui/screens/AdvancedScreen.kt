@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.NetworkCheck
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
import com.maya.clone.ui.viewmodel.AdvancedViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun AdvancedScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdvancedViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val debugMode by viewModel.debugMode.collectAsStateWithLifecycle(false)
    val showFps by viewModel.showFps.collectAsStateWithLifecycle(false)
    val logLevel by viewModel.logLevel.collectAsStateWithLifecycle("info")
    val experimentalFeatures by viewModel.experimentalFeatures.collectAsStateWithLifecycle(false)
    val networkLogging by viewModel.networkLogging.collectAsStateWithLifecycle(false)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    var showLogLevelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Advanced", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Debug")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading advanced settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Debug Mode",
                subtitle = "Enable verbose logging and diagnostics",
                checked = debugMode,
                onCheckedChange = { viewModel.setDebugMode(it) }
            )
            SettingsToggle(
                label = "Show FPS",
                subtitle = "Display frame rate overlay",
                checked = showFps,
                onCheckedChange = { viewModel.setShowFps(it) }
            )
            SettingsToggle(
                label = "Network Logging",
                subtitle = "Log all network requests and responses",
                checked = networkLogging,
                onCheckedChange = { viewModel.setNetworkLogging(it) }
            )
            SettingsToggle(
                label = "Experimental Features",
                subtitle = "Enable unreleased features (may be unstable)",
                checked = experimentalFeatures,
                onCheckedChange = { viewModel.setExperimentalFeatures(it) }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Logging")

            SettingsRow(
                label = "Log Level",
                subtitle = logLevel.capitalize(),
                leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: show dialog */ }
            )

            SettingsRow(
                label = "Export Logs",
                subtitle = "Save logs to file for debugging",
                leadingIcon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: export logs */ }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Experimental")

            SettingsRow(
                label = "Reset All Settings",
                subtitle = "Restore all settings to defaults",
                leadingIcon = { Icon(Icons.Default.Science, contentDescription = null, tint = Error) },
                onClick = { /* TODO: reset settings dialog */ }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}