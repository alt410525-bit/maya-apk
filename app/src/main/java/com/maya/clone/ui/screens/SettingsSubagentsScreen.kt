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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
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
import com.maya.clone.ui.viewmodel.SettingsSubagentsViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun SettingsSubagentsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsSubagentsViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val subagents by viewModel.subagents.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showCreateDialog by remember { mutableStateOf(false) }
    var newAgentName by remember { mutableStateOf("") }
    var newAgentPrompt by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Sub-Agents", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Your Sub-Agents (${subagents.size})")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading sub-agents...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (subagents.isEmpty()) {
            SettingsRow(
                label = "No sub-agents created",
                subtitle = "Create your first custom agent",
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newAgentName = ""
                    newAgentPrompt = ""
                    showCreateDialog = true
                }
            )
        } else {
            subagents.forEach { agent ->
                SubagentCard(
                    agent = agent,
                    onDelete = { viewModel.deleteSubagent(agent.id) },
                    onToggle = { viewModel.toggleSubagentActive(agent) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create new
        SettingsRow(
            label = "Create Custom Agent",
            subtitle = "Build a specialized AI agent",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = {
                newAgentName = ""
                newAgentPrompt = ""
                showCreateDialog = true
            }
        )

        // Create dialog
        if (showCreateDialog) {
            CreateSubagentDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, prompt ->
                    showCreateDialog = false
                    viewModel.createSubagent(name, prompt)
                },
                name = newAgentName,
                onNameChange = { newAgentName = it },
                prompt = newAgentPrompt,
                onPromptChange = { newAgentPrompt = it }
            )
        }
    }
}

@Composable
private fun SubagentCard(
    agent: com.maya.clone.data.entity.AgentEntity,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    val isActive = agent.isActive

    SettingsRow(
        label = agent.name,
        subtitle = "Model: ${agent.model} • ${agent.systemPrompt.take(50).ifEmpty { "No prompt" }}...",
        leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null, tint = NeonPurple) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Switch(
                    checked = isActive,
                    onCheckedChange = { onToggle() },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = NeonPurple,
                        checkedTrackColor = NeonPurple.copy(alpha = 0.3f)
                    )
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, tint = Error, contentDescription = "Delete ${agent.name}")
                }
            }
        },
        onClick = { /* navigate to agent details */ }
    )
}

@Composable
private fun CreateSubagentDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    prompt: String,
    onPromptChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Sub-Agent", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Agent Name", color = TextSecondary) },
                    placeholder = { Text("e.g., Code Reviewer", color = TextTertiary) },
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
                    value = prompt,
                    onValueChange = onPromptChange,
                    label = { Text("System Prompt (optional)", color = TextSecondary) },
                    placeholder = { Text("Custom instructions...", color = TextTertiary) },
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
                    if (name.isNotBlank()) {
                        onCreate(name, prompt)
                    }
                },
                enabled = name.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
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