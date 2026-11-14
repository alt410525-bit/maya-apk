@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Security
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
import com.maya.clone.ui.viewmodel.KeySetupViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun KeySetupScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KeySetupViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val encryptionKey by viewModel.encryptionKey.collectAsStateWithLifecycle("")
    val keyRotationInterval by viewModel.keyRotationInterval.collectAsStateWithLifecycle(90)
    val autoRotate by viewModel.autoRotate.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    var showGenerateDialog by remember { mutableStateOf(false) }
    var showRotateIntervalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Key Setup", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Encryption Key")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading key settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            if (encryptionKey.isEmpty()) {
                SettingsRow(
                    label = "No encryption key",
                    subtitle = "Generate a key to encrypt your data",
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple) },
                    onClick = { showGenerateDialog = true }
                )
            } else {
                SettingsRow(
                    label = "Encryption Key",
                    subtitle = encryptionKey.take(16) + "...",
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple) },
                    trailing = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /* TODO: copy key */ }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextTertiary)
                            }
                            IconButton(onClick = { /* TODO: show full key */ }) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = TextTertiary)
                            }
                        }
                    }
                )
            }

            // Generate new key button
            SettingsRow(
                label = "Generate New Key",
                subtitle = "Replace current encryption key",
                leadingIcon = { Icon(Icons.Default.RotateRight, contentDescription = null, tint = NeonPurple) },
                onClick = { showGenerateDialog = true }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Key Rotation")

            SettingsToggle(
                label = "Auto-Rotate Keys",
                subtitle = "Automatically generate new keys periodically",
                checked = autoRotate,
                onCheckedChange = { /* TODO */ }
            )

            SettingsRow(
                label = "Rotation Interval",
                subtitle = "$keyRotationInterval days",
                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = NeonPurple) },
                onClick = { showRotateIntervalDialog = true }
            )
        }

        // Generate key confirmation
        if (showGenerateDialog) {
            GenerateKeyDialog(
                onDismiss = { showGenerateDialog = false },
                onGenerate = {
                    showGenerateDialog = false
                    viewModel.generateNewKey()
                }
            )
        }

        // Rotation interval dialog
        if (showRotateIntervalDialog) {
            RotationIntervalDialog(
                currentValue = keyRotationInterval,
                onValueChange = { /* TODO */ },
                onDismiss = { showRotateIntervalDialog = false }
            )
        }
    }
}

@Composable
private fun GenerateKeyDialog(
    onDismiss: () -> Unit,
    onGenerate: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate New Key", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "This will generate a new encryption key and re-encrypt all your data. " +
                    "The old key will be permanently deleted. This action cannot be undone.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = { onGenerate() },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = NeonPurple,
                    contentColor = Color.Black
                )
            ) {
                Text("Generate", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel", color = NeonPurple)
            }
        }
    )
}

@Composable
private fun RotationIntervalDialog(
    currentValue: Int,
    onValueChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val intervals = listOf(7, 30, 60, 90, 180, 365)

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Key Rotation Interval", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                intervals.forEach { days ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .clickable { /* onValueChange(days) */ }
                            .background(Color.Transparent)
                    ) {
                        Text("$days days", color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel", color = NeonPurple) }
        },
        dismissButton = {
            androidx.compose.material3.Button(onClick = onDismiss,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.Black)
            ) { Text("Done", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}