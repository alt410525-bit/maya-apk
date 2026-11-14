@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.maya.clone.ui.viewmodel.SettingsBehaviourViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsBehaviourScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsBehaviourViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val alwaysListening by viewModel.alwaysListening.collectAsStateWithLifecycle(true)
    val interruptible by viewModel.interruptible.collectAsStateWithLifecycle(true)
    val autoAwaken by viewModel.autoAwaken.collectAsStateWithLifecycle(false)
    val memoryLimit by viewModel.memoryLimit.collectAsStateWithLifecycle(75)
    val forgetAfterDays by viewModel.forgetAfterDays.collectAsStateWithLifecycle(30)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Behaviour", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Startup")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading behaviour settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Start Maya on Boot",
                subtitle = "Launch Maya automatically when device starts",
                checked = false,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Quick Start",
                subtitle = "Skip intro on app launch",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Interaction")

            SettingsToggle(
                label = "Echo Mode",
                subtitle = "Maya repeats your words",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Typing Suggestions",
                subtitle = "Show word suggestions while typing",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Haptic Feedback",
                subtitle = "Vibrate on interactions",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Smart Features")

            SettingsToggle(
                label = "Smart Replies",
                subtitle = "Suggested responses to messages",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Context Awareness",
                subtitle = "Understand conversation context",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Wake Word Detection",
                subtitle = "Detect when to wake Maya",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Memory")

            SettingsRow(
                label = "Memory Limit",
                subtitle = "${memoryLimit}%",
                leadingIcon = { Icon(Icons.Default.Storage, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: number picker */ }
            )
            SettingsRow(
                label = "Forget After",
                subtitle = "$forgetAfterDays days",
                leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: number picker */ }
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