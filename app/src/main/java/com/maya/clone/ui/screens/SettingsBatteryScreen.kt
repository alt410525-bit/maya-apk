@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Settings
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
import com.maya.clone.ui.viewmodel.SettingsBatteryViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsBatteryScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsBatteryViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val isOptimized by viewModel.isOptimized.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Battery", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Battery Optimization")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading battery settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Battery Optimization",
                subtitle = "Allow system to optimize app for battery",
                checked = isOptimized,
                onCheckedChange = { viewModel.setOptimized(it) }
            )

            SettingsRow(
                label = "Exempt from Optimization",
                subtitle = "Keep Maya running in background",
                leadingIcon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = NeonPurple) },
                onClick = { viewModel.requestExemption() }
            )
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Power Management")

        SettingsToggle(
            label = "Background Refresh",
            subtitle = "Allow app background tasks",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Auto-Sync",
            subtitle = "Automatically sync data",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Low Power Mode",
            subtitle = "Activate at 20% battery",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Battery Usage")

        // Stats display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark)
                .clip(RoundedCornerShape(16.dp))
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                BatteryStatRow(label = "Today", usage = "15%")
                BatteryStatRow(label = "Last 7 days", usage = "42%")
                BatteryStatRow(label = "Screen-on time", usage = "3h 24m")
            }
        }
    }
}

@Composable
private fun BatteryStatRow(label: String, usage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark)
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, color = TextPrimary, fontSize = 14.sp)
            Text(text = usage, color = TextSecondary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = usage, color = NeonPurple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}