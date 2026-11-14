@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.maya.clone.ui.viewmodel.SettingsTouchGuardViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle
import com.maya.clone.ui.components.SettingsSlider

@Composable
fun SettingsTouchGuardScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsTouchGuardViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val enabled by viewModel.enabled.collectAsStateWithLifecycle(false)
    val sensitivity by viewModel.sensitivity.collectAsStateWithLifecycle(0.5f)
    val protectedZones by viewModel.protectedZones.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    var showAddZoneDialog by remember { mutableStateOf(false) }
    var newZone by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Touch Guard", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Protection")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading touch guard settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Touch Guard",
                subtitle = "Enable touch guard protection",
                checked = enabled,
                onCheckedChange = { viewModel.setEnabled(it) }
            )
            SettingsSlider(
                label = "Sensitivity",
                value = sensitivity,
                onValueChange = { viewModel.setSensitivity(it) },
                valueRange = 0.1f..1.0f,
                steps = 9,
                valueLabel = "${(sensitivity * 100).toInt()}%"
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Protected Zones (${protectedZones.size})")

            if (protectedZones.isEmpty()) {
                SettingsRow(
                    label = "No protected zones",
                    subtitle = "Add screen areas to protect from accidental touches",
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                    onClick = {
                        newZone = ""
                        showAddZoneDialog = true
                    }
                )
            } else {
                protectedZones.forEach { zone ->
                    SettingsRow(
                        label = zone,
                        subtitle = "Protected",
                        leadingIcon = { Icon(Icons.Default.ToggleOn, contentDescription = null, tint = NeonPurple) },
                        trailing = {
                            IconButton(onClick = { viewModel.removeProtectedZone(zone) }) {
                                Icon(Icons.Default.Delete, tint = Error, contentDescription = "Remove $zone")
                            }
                        }
                    )
                }
            }

            // Add zone button
            SettingsRow(
                label = "Add Protected Zone",
                subtitle = "Define a screen area to protect",
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newZone = ""
                    showAddZoneDialog = true
                }
            )

            // Add zone dialog
            if (showAddZoneDialog) {
                AddZoneDialog(
                    onDismiss = { showAddZoneDialog = false },
                    onAdd = { zone ->
                        showAddZoneDialog = false
                        viewModel.addProtectedZone(zone)
                    },
                    zone = newZone,
                    onZoneChange = { newZone = it }
                )
            }
        }
    }
}

@Composable
private fun AddZoneDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    zone: String,
    onZoneChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Protected Zone", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter zone name (e.g., 'bottom_bar', 'notification_shade'):", color = TextSecondary, fontSize = 14.sp)
                androidx.compose.material3.TextField(
                    value = zone,
                    onValueChange = onZoneChange,
                    label = { Text("Zone Name", color = TextSecondary) },
                    placeholder = { Text("e.g., bottom_bar", color = TextTertiary) },
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
                onClick = {
                    if (zone.isNotBlank()) {
                        onAdd(zone)
                    }
                },
                enabled = zone.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (zone.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Add", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}