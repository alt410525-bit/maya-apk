@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
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
import com.maya.clone.ui.viewmodel.SettingsFaceAttemptsViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsFaceAttemptsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsFaceAttemptsViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val maxAttempts by viewModel.maxAttempts.collectAsStateWithLifecycle(5)
    val lockoutDuration by viewModel.lockoutDuration.collectAsStateWithLifecycle(30)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Face Attempts", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Failed Attempts")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading face attempts...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            // In real app, this would come from FaceLockAttemptRepository
            // For now, show placeholder
            SettingsRow(
                label = "No failed attempts recorded",
                subtitle = "Failed face unlock attempts will appear here",
                leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = NeonPurple) }
            )
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Security Settings")

        SettingsRow(
            label = "Max Attempts Before Lock",
            subtitle = "$maxAttempts attempts",
            trailing = { Text(maxAttempts.toString(), color = TextSecondary, fontSize = 14.sp) }
        )
        SettingsRow(
            label = "Lockout Duration",
            subtitle = "$lockoutDuration minutes",
            trailing = { Text("$lockoutDuration min", color = TextSecondary, fontSize = 14.sp) }
        )
        SettingsToggle(
            label = "Capture Photo on Failed Attempt",
            subtitle = "Take photo when face unlock fails",
            checked = true,
            onCheckedChange = { /* TODO */ }
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