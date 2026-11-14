@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import com.maya.clone.ui.viewmodel.SettingsScreenLockViewModel
import com.maya.clone.ui.components.SettingsToggle
import com.maya.clone.ui.components.SettingsSlider

@Composable
fun SettingsScreenLockScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenLockViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val lockType by viewModel.lockType.collectAsStateWithLifecycle("pin")
    val autoLockDelay by viewModel.autoLockDelay.collectAsStateWithLifecycle(5)
    val showLockScreen by viewModel.showLockScreen.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Screen Lock", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Lock Type")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading screen lock settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LockTypeCard(
                    title = "PIN",
                    icon = Icons.Default.PushPin,
                    isSelected = lockType == "pin",
                    onSelect = { viewModel.setLockType("pin") }
                )
                LockTypeCard(
                    title = "Pattern",
                    icon = Icons.Default.Apps,
                    isSelected = lockType == "pattern",
                    onSelect = { viewModel.setLockType("pattern") }
                )
                LockTypeCard(
                    title = "Biometric",
                    icon = Icons.Default.Fingerprint,
                    isSelected = lockType == "biometric",
                    onSelect = { viewModel.setLockType("biometric") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Auto-Lock")

        SettingsSlider(
            label = "Auto-Lock Delay",
            value = autoLockDelay.toFloat(),
            onValueChange = { viewModel.setAutoLockDelay(it.toInt()) },
            valueRange = 0f..30f,
            steps = 30,
            valueLabel = "${autoLockDelay} min"
        )

        SettingsToggle(
            label = "Show Lock Screen",
            subtitle = "Require unlock when app is opened",
            checked = showLockScreen,
            onCheckedChange = { viewModel.setShowLockScreen(it) }
        )
    }
}

@Composable
private fun LockTypeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgColor = if (isSelected) NeonPurple else CardDark
    val textColor = if (isSelected) Color.White else TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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