@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
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
import com.maya.clone.ui.viewmodel.SettingsMayaViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle
import com.maya.clone.ui.components.SettingsSlider

@Composable
fun SettingsMayaScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsMayaViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val model by viewModel.model.collectAsStateWithLifecycle("gpt-4o-mini")
    val temperature by viewModel.temperature.collectAsStateWithLifecycle(0.7f)
    val maxTokens by viewModel.maxTokens.collectAsStateWithLifecycle(2048)
    val systemPrompt by viewModel.systemPrompt.collectAsStateWithLifecycle("")
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showModelDialog by remember { mutableStateOf(false) }
    var showSystemPromptDialog by remember { mutableStateOf(false) }
    var editSystemPrompt by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Maya Settings", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Model")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading Maya settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsRow(
                label = "Model",
                subtitle = model,
                leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null, tint = NeonPurple) },
                onClick = { showModelDialog = true }
            )

            SettingsSlider(
                label = "Temperature",
                value = temperature,
                onValueChange = { viewModel.setTemperature(it) },
                valueRange = 0.0f..2.0f,
                steps = 20,
                valueLabel = "%.1f".format(temperature)
            )

            SettingsRow(
                label = "Max Tokens",
                subtitle = maxTokens.toString(),
                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: number picker */ }
            )

            SettingsRow(
                label = "System Prompt",
                subtitle = systemPrompt.ifEmpty { "Default" }.take(50) + "...",
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    editSystemPrompt = systemPrompt
                    showSystemPromptDialog = true
                }
            )
        }

        // Model selection dialog
        if (showModelDialog) {
            ModelSelectionDialog(
                currentValue = model,
                onValueChange = { viewModel.setModel(it); showModelDialog = false },
                onDismiss = { showModelDialog = false }
            )
        }

        // System prompt dialog
        if (showSystemPromptDialog) {
            EditTextDialog(
                title = "System Prompt",
                value = editSystemPrompt,
                onValueChange = { editSystemPrompt = it },
                onConfirm = {
                    viewModel.setSystemPrompt(editSystemPrompt)
                    showSystemPromptDialog = false
                },
                onDismiss = { showSystemPromptDialog = false },
                hint = "Enter custom system prompt..."
            )
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Behaviour
        SectionHeader(title = "Behaviour")

        SettingsToggle(
            label = "Always Listening",
            subtitle = "Maya listens for wake word",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Interruptible Responses",
            subtitle = "Can interrupt Maya mid-response",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsRow(
            label = "Auto Awaken",
            subtitle = "Wake when you approach",
            trailing = {
                Text("Disabled", color = TextTertiary, fontSize = 14.sp)
            }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Memory
        SectionHeader(title = "Memory")

        SettingsRow(
            label = "Memory Limit",
            subtitle = "75%",
            trailing = {
                Text("75%", color = TextSecondary, fontSize = 14.sp)
            }
        )
        SettingsRow(
            label = "Forget After",
            subtitle = "30 days",
            trailing = {
                Text("30 days", color = TextSecondary, fontSize = 14.sp)
            }
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
private fun ModelSelectionDialog(
    currentValue: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val models = listOf("gpt-4o-mini", "gpt-4o", "gpt-4-turbo", "gpt-3.5-turbo", "claude-3-haiku", "claude-3-sonnet", "custom")

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Model", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                models.forEach { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .clickable { onValueChange(m) }
                            .background(if (m == currentValue) NeonPurple.copy(alpha = 0.1f) else Color.Transparent)
                    ) {
                        Text(m, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (m == currentValue) {
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
                    maxLines = 4
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