@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
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
import com.maya.clone.ui.viewmodel.SettingsAutoReplyViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle
import com.maya.clone.ui.components.SettingsSlider

@Composable
fun SettingsAutoReplyScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsAutoReplyViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val enabledRules by viewModel.enabledRules.collectAsStateWithLifecycle(emptyList())
    val allRules by viewModel.allRules.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    var showCreateDialog by remember { mutableStateOf(false) }
    var newRuleName by remember { mutableStateOf("") }
    var newRuleCondition by remember { mutableStateOf("") }
    var newRuleAction by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Auto-Reply", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "General")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading auto-reply settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Auto-Reply Enabled",
                subtitle = "Automatically reply to messages when conditions match",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsSlider(
                label = "Reply Delay (seconds)",
                value = 5f,
                onValueChange = { /* TODO */ },
                valueRange = 0f..30f,
                steps = 0,
                valueLabel = "5s"
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Rules (${enabledRules.size} active / ${allRules.size} total)")

            if (allRules.isEmpty()) {
                SettingsRow(
                    label = "No rules created",
                    subtitle = "Create your first auto-reply rule",
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                    onClick = {
                        newRuleName = ""
                        newRuleCondition = ""
                        newRuleAction = ""
                        showCreateDialog = true
                    }
                )
            } else {
                allRules.forEach { rule ->
                    AutoReplyRuleRow(
                        rule = rule,
                        isEnabled = rule.enabled,
                        onToggle = { /* TODO: toggle enabled */ },
                        onEdit = { /* TODO: edit dialog */ },
                        onDelete = { viewModel.deleteRule(rule.id) }
                    )
                }
            }

            // Add new rule button
            SettingsRow(
                label = "Create Rule",
                subtitle = "Define a new auto-reply condition and action",
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newRuleName = ""
                    newRuleCondition = ""
                    newRuleAction = ""
                    showCreateDialog = true
                }
            )

            // Create rule dialog
            if (showCreateDialog) {
                CreateAutoReplyDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreate = { name, condition, action ->
                        showCreateDialog = false
                        val rule = com.maya.clone.data.entity.AutoReplyRuleEntity(
                            id = java.util.UUID.randomUUID().toString(),
                            userId = userId,
                            name = name,
                            conditionsJson = condition,
                            actionsJson = action,
                            priority = 0,
                            enabled = true
                        )
                        viewModel.saveRule(rule)
                    },
                    name = newRuleName,
                    onNameChange = { newRuleName = it },
                    condition = newRuleCondition,
                    onConditionChange = { newRuleCondition = it },
                    action = newRuleAction,
                    onActionChange = { newRuleAction = it }
                )
            }
        }
    }
}

@Composable
private fun AutoReplyRuleRow(
    rule: com.maya.clone.data.entity.AutoReplyRuleEntity,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SettingsRow(
        label = rule.name,
        subtitle = "Condition: ${rule.conditionsJson.take(50)}... → Action: ${rule.actionsJson.take(50)}...",
        leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = NeonPurple) },
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
                    Icon(Icons.Default.Edit, tint = NeonPurple, contentDescription = "Edit rule")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, tint = Error, contentDescription = "Delete rule")
                }
            }
        }
    )
}

@Composable
private fun CreateAutoReplyDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    condition: String,
    onConditionChange: (String) -> Unit,
    action: String,
    onActionChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Auto-Reply Rule", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Rule Name", color = TextSecondary) },
                    placeholder = { Text("e.g., Work Hours Auto-Reply", color = TextTertiary) },
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

                androidx.compose.material3.TextField(
                    value = condition,
                    onValueChange = onConditionChange,
                    label = { Text("Condition (JSON)", color = TextSecondary) },
                    placeholder = { Text("e.g., {\"time\": \"9-17\", \"days\": \"mon-fri\"}", color = TextTertiary) },
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
                    placeholder = { Text("e.g., {\"type\": \"reply\", \"message\": \"I'm busy\"}", color = TextTertiary) },
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
                        onCreate(name, condition, action)
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