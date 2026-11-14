@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SensorOccupied
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
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
import com.maya.clone.ui.viewmodel.SettingsAwarenessViewModel
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsAwarenessScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsAwarenessViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val enabled by viewModel.enabled.collectAsStateWithLifecycle(false)
    val locationEnabled by viewModel.locationEnabled.collectAsStateWithLifecycle(true)
    val sensorEnabled by viewModel.sensorEnabled.collectAsStateWithLifecycle(true)
    val appUsageEnabled by viewModel.appUsageEnabled.collectAsStateWithLifecycle(true)
    val notificationEnabled by viewModel.notificationEnabled.collectAsStateWithLifecycle(true)
    val callEnabled by viewModel.callEnabled.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Awareness", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Context Awareness")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading awareness settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Enable Awareness",
                subtitle = "Allow Maya to understand your context",
                checked = enabled,
                onCheckedChange = { viewModel.setEnabled(it) }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Data Sources")

            SettingsToggle(
                label = "Location",
                subtitle = "Use GPS for location-aware suggestions",
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = NeonPurple) },
                checked = locationEnabled,
                onCheckedChange = { viewModel.setLocationEnabled(it) }
            )
            SettingsToggle(
                label = "Sensors",
                subtitle = "Accelerometer, gyroscope, light, proximity",
                leadingIcon = { Icon(Icons.Default.SensorOccupied, contentDescription = null, tint = NeonPurple) },
                checked = sensorEnabled,
                onCheckedChange = { viewModel.setSensorEnabled(it) }
            )
            SettingsToggle(
                label = "App Usage",
                subtitle = "Track which apps you use and when",
                leadingIcon = { Icon(Icons.Default.Apps, contentDescription = null, tint = NeonPurple) },
                checked = appUsageEnabled,
                onCheckedChange = { viewModel.setAppUsageEnabled(it) }
            )
            SettingsToggle(
                label = "Notifications",
                subtitle = "Analyze notification patterns",
                leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = NeonPurple) },
                checked = notificationEnabled,
                onCheckedChange = { viewModel.setNotificationEnabled(it) }
            )
            SettingsToggle(
                label = "Calls",
                subtitle = "Detect incoming/outgoing calls for context",
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonPurple) },
                checked = callEnabled,
                onCheckedChange = { viewModel.setCallEnabled(it) }
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