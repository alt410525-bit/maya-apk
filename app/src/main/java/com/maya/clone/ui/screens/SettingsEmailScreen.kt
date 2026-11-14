@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import com.maya.clone.ui.viewmodel.SettingsEmailViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsEmailScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsEmailViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val isEnabled by viewModel.isEnabled.collectAsStateWithLifecycle(false)
    val emailAddress by viewModel.emailAddress.collectAsStateWithLifecycle("")
    val syncFrequency by viewModel.syncFrequency.collectAsStateWithLifecycle("15min")
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showEditEmailDialog by remember { mutableStateOf(false) }
    var editEmail by remember { mutableStateOf("") }

    var showSyncFrequencyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Email & Alerts", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        error?.let { errorMsg ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) { Text("Error: $errorMsg", color = Error, fontSize = 12.sp) }
        }

        SectionHeader(title = "Email Account")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading email settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Email Integration",
                subtitle = "Connect your email account for sync and alerts",
                checked = isEnabled,
                onCheckedChange = { viewModel.setEnabled(it) }
            )

            SettingsRow(
                label = "Email Address",
                subtitle = emailAddress.ifEmpty { "Not connected" },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                onClick = {
                    editEmail = emailAddress
                    showEditEmailDialog = true
                }
            )

            SettingsRow(
                label = "Sync Frequency",
                subtitle = syncFrequency,
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = NeonPurple) },
                onClick = { showSyncFrequencyDialog = true }
            )

            SettingsToggle(
                label = "New Message Notifications",
                subtitle = "Get notified of incoming emails",
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
            )
        }

        // Edit email dialog
        if (showEditEmailDialog) {
            EditTextDialog(
                title = "Email Address",
                value = editEmail,
                onValueChange = { editEmail = it },
                onConfirm = {
                    viewModel.setEmailAddress(editEmail)
                    showEditEmailDialog = false
                },
                onDismiss = { showEditEmailDialog = false },
                hint = "you@example.com"
            )
        }

        // Sync frequency dialog
        if (showSyncFrequencyDialog) {
            SyncFrequencyDialog(
                currentValue = syncFrequency,
                onValueChange = { viewModel.setSyncFrequency(it); showSyncFrequencyDialog = false },
                onDismiss = { showSyncFrequencyDialog = false }
            )
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Push Notifications
        SectionHeader(title = "Push Notifications")

        SettingsToggle(
            label = "New Message Alerts",
            subtitle = "Notify when new messages arrive",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Delivery Notifications",
            subtitle = "Get notified of important deliveries",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Mention Notifications",
            subtitle = "Alert when @mentioned",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Alert Settings
        SectionHeader(title = "Alert Settings")

        SettingsToggle(
            label = "Sound Alerts",
            subtitle = "Play sound on new messages",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Vibration",
            subtitle = "Vibrate on new messages",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Preview Messages",
            subtitle = "Show preview in notification shade",
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

@Composable
private fun EditTextDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    hint: String
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(title, color = TextSecondary) },
                    placeholder = { Text(hint, color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = DividerColor,
                        cursorColor = NeonPurple,
                        focusedTextColor = TextPrimary,
 unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TextSecondary,
 unfocusedLabelColor = TextSecondary,
                        focusedPlaceholderColor = TextTertiary,
 unfocusedPlaceholderColor = TextTertiary,
                        focusedContainerColor = CardDark,
 unfocusedContainerColor = CardDark
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = { focusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = onConfirm,
                enabled = value.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (value.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
private fun SyncFrequencyDialog(
    currentValue: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val frequencies = listOf("5min", "15min", "30min", "1hour", "6hour", "12hour", "24hour", "manual")

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sync Frequency", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                frequencies.forEach { freq ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .clickable { onValueChange(freq) }
                            .background(if (freq == currentValue) NeonPurple.copy(alpha = 0.1f) else Color.Transparent)
                    ) {
                        Text(freq, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (freq == currentValue) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple, modifier = Modifier.padding(16.dp))
                        }
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