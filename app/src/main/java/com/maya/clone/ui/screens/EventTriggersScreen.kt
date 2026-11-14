@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Bolt
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
import com.maya.clone.ui.viewmodel.EventTriggersViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun EventTriggersScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventTriggersViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val enabledTriggers by viewModel.enabledTriggers.collectAsStateWithLifecycle(emptyList())
    val allTriggers by viewModel.allTriggers.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showCreateDialog by remember { mutableStateOf(false) }
    var newTriggerName by remember { mutableStateOf("") }
    var newTriggerEventType by remember { mutableStateOf("location") }
    var newTriggerCondition by remember { mutableStateOf("") }
    var newTriggerAction by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Event Triggers", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Triggers (${enabledTriggers.size} active / ${allTriggers.size} total)")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading triggers...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (allTriggers.isEmpty()) {
            SettingsRow(
                label = "No triggers created",
                subtitle = "Create automation rules based on events",
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newTriggerName = ""
                    newTriggerEventType = "location"
                    newTriggerCondition = ""
                    newTriggerAction = ""
                    showCreateDialog = true
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allTriggers, key = { it.id }) { trigger ->
                    TriggerRow(
                        trigger = trigger,
                        isEnabled = trigger.enabled,
                        onToggle = { /* TODO */ },
                        onEdit = { /* TODO */ },
                        onDelete = { viewModel.deleteTrigger(trigger.id) }
                    )
                }
            }
        }

        // Create new trigger button
        SettingsRow(
            label = "Create Trigger",
            subtitle = "Automate actions based on events",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = {
                newTriggerName = ""
                newTriggerEventType = "location"
                newTriggerCondition = ""
                newTriggerAction = ""
                showCreateDialog = true
            }
        )

        // Create trigger dialog
        if (showCreateDialog) {
            CreateTriggerDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, eventType, condition, action ->
                    showCreateDialog = false
                    val trigger = com.maya.clone.data.entity.TriggerEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = userId,
                        name = name,
                        eventType = eventType,
                        conditionsJson = condition,
                        actionsJson = action,
                        enabled = true
                    )
                    viewModel.saveTrigger(trigger)
                },
                name = newTriggerName,
                onNameChange = { newTriggerName = it },
                eventType = newTriggerEventType,
                onEventTypeChange = { newTriggerEventType = it },
                condition = newTriggerCondition,
                onConditionChange = { newTriggerCondition = it },
                action = newTriggerAction,
                onActionChange = { newTriggerAction = it }
            )
        }
    }
}

@Composable
private fun TriggerRow(
    trigger: com.maya.clone.data.entity.TriggerEntity,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SettingsRow(
        label = trigger.name,
        subtitle = "Event: ${trigger.eventType} • ${trigger.conditionsJson.take(40)}... → ${trigger.actionsJson.take(40)}...",
        leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null, tint = NeonPurple) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Switch(
                    checked = isEnabled,
                    onCheckedChange = { _ -> onToggle() },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = NeonPurple,
                        checkedTrackColor = NeonPurple.copy(alpha = 0.3f)
                    )
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, tint = NeonPurple, contentDescription = "Edit trigger")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, tint = Error, contentDescription = "Delete trigger")
                }
            }
        }
    )
}

@Composable
private fun CreateTriggerDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    eventType: String,
    onEventTypeChange: (String) -> Unit,
    condition: String,
    onConditionChange: (String) -> Unit,
    action: String,
    onActionChange: (String) -> Unit
) {
    val eventTypes = listOf("location", "sensor", "app", "notification", "call")
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Trigger", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Trigger Name", color = TextSecondary) },
                    placeholder = { Text("e.g., Home Arrival", color = TextTertiary) },
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
                        onNext = { focusRequester.requestFocus() }
                    ),
                    singleLine = true
                )

                // Event type dropdown
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { /* TODO */ }
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = eventType,
                        onValueChange = onEventTypeChange,
                        label = { Text("Event Type", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            androidx.compose.material3.ExposedDropdownMenuBox(
                                expanded = false,
                                onExpandedChange = { /* TODO */ }
                            ) {
                                androidx.compose.material3.Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = DividerColor,
                            cursorColor = NeonPurple,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = TextSecondary,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = CardDark,
                            unfocusedContainerColor = CardDark
                        )
                    ) {
                        androidx.compose.material3.Text(eventType.capitalize(), color = TextPrimary)
                    }
                }

                androidx.compose.material3.TextField(
                    value = condition,
                    onValueChange = onConditionChange,
                    label = { Text("Condition (JSON)", color = TextSecondary) },
                    placeholder = { Text("e.g., {\"place\": \"home\"}", color = TextTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                        onNext = { focusRequester.requestFocus() }
                    ),
                    singleLine = true
                )

                androidx.compose.material3.TextField(
                    value = action,
                    onValueChange = onActionChange,
                    label = { Text("Action (JSON)", color = TextSecondary) },
                    placeholder = { Text("e.g., {\"type\": \"notify\", \"message\": \"Welcome home\"}", color = TextTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(100.dp),
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
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
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
                onClick = {
                    if (name.isNotBlank() && condition.isNotBlank() && action.isNotBlank()) {
                        onCreate(name, eventType, condition, action)
                    }
                },
                enabled = name.isNotBlank() && condition.isNotBlank() && action.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank() && condition.isNotBlank() && action.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Create", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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