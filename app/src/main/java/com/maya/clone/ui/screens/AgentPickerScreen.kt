@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.keyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maya.clone.Navigation
import com.maya.clone.data.entity.AgentEntity
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.theme.NeonPurple
import com.maya.clone.ui.theme.TextPrimary
import com.maya.clone.ui.theme.TextSecondary
import com.maya.clone.ui.theme.AwarenessColor
import com.maya.clone.ui.viewmodel.AgentPickerViewModel
import kotlinx.coroutines.flow.combine
import com.maya.clone.R
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AgentPickerScreen(
    userId: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgentPickerViewModel = hiltViewModel()
) {
    viewModel.init(userId)
    
    val agents by viewModel.agents.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)
    
    var selectedAgent by remember { mutableStateOf<String?>(null) }
    var scrollState by remember { rememberScrollState(it) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newAgentName by remember { mutableStateOf("") }
    var newAgentSystemPrompt by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // ── Top bar ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(CardDark, CircleShape)) {
                Icon(Icons.Default.ArrowBack, tint = TextSecondary, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Choose Agent", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            // Create new agent button
            IconButton(
                onClick = {
                    newAgentName = ""
                    newAgentSystemPrompt = ""
                    showCreateDialog = true
                },
                modifier = Modifier.size(40.dp).background(CardDark, CircleShape)
            ) {
                Icon(Icons.Default.Add, tint = NeonPurple, contentDescription = "Create agent")
            }
        }
        HorizontalDivider(color = DividerColor)

        // ── Error display ──────────────────────────────────
        if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Error: $error",
                    color = NeonPurple,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // ── Grid of agents ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // 3-column grid
            val gridCells = remember { GridCells.Adaptive(3) }
            if (isLoading) {
                // Loading skeleton
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonPurple, modifier = Modifier.size(48.dp))
                }
            } else if (agents.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(NeonPurple.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PersonAdd, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(40.dp))
                        }
                        Text("No agents yet", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Create your first AI agent to get started", color = TextTertiary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                newAgentName = ""
                                newAgentSystemPrompt = ""
                                showCreateDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Create Agent", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spaceEvenly
                ) {
                    items(agents, key = { it.id }) { agent ->
                        AgentCard(
                            agent = agent,
                            isSelected = selectedAgent == agent.id,
                            onClick = {
                                selectedAgent = if (selectedAgent == agent.id) null else agent.id
                            },
                            onLongClick = { },
                            onDelete = { viewModel.deleteAgent(agent.id) },
                            onToggleActive = { viewModel.toggleAgentActive(agent) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Action bar ─────────────────────────────────────
        if (selectedAgent != null) {
            val selectedAgentEntity = agents.find { it.id == selectedAgent }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { onSelect(selectedAgent); onBack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Use ${selectedAgentEntity?.name ?: selectedAgent}", color = NeonPurple, fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = { selectedAgent = null },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = TextTertiary, fontSize = 14.sp)
                }
            }
        } else {
            Button(
                onClick = { /* nothing — need selection */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            ) {
                Text("Select an agent", color = TextTertiary, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }

    // ── Create Agent BottomSheet ───────────────────────────
    if (showCreateDialog) {
        CreateAgentBottomSheet(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, systemPrompt ->
                viewModel.createAgent(name, systemPrompt)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun AgentCard(
    agent: AgentEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) NeonPurple.copy(alpha = 0.15f) else CardDark,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Agent icon — load from assets based on name
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(NeonPurple.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = when (agent.name.lowercase().replace(" ", "_")) {
                        "robot" -> R.drawable.agent_robot
                        "ninja" -> R.drawable.agent_ninja
                        "scientist" -> R.drawable.agent_scientist
                        "detective" -> R.drawable.agent_man_detective
                        "health_worker", "teacher" -> R.drawable.agent_health_worker
                        "judge", "artist" -> R.drawable.agent_judge
                        "guard", "chef" -> R.drawable.agent_guard
                        "mechanic", "pilot" -> R.drawable.agent_mechanic
                        "money_face" -> R.drawable.agent_money_mouth_face
                        "nerd_face" -> R.drawable.agent_nerd_face
                        "office_worker" -> R.drawable.agent_office_worker
                        "person_genie", "genie" -> R.drawable.agent_person_genie
                        "person_mage", "mage" -> R.drawable.agent_person_mage
                        "person_superhero", "superhero" -> R.drawable.agent_person_superhero
                        "alien", "agent_1" -> R.drawable.agent_alien
                        "astronaut" -> R.drawable.agent_astronaut
                        "cat_with_wry_smile", "cat_smile" -> R.drawable.agent_cat_with_wry_smile
                        "cook", "chef" -> R.drawable.agent_cook
                        "ghost" -> R.drawable.agent_ghost
                        "singer" -> R.drawable.agent_singer
                        "student" -> R.drawable.agent_student
                        "technologist", "woman_technologist" -> R.drawable.agent_woman_technologist
                        "brain" -> R.drawable.agent_brain
                        else -> R.drawable.maya_logo
                    }
                ),
                contentDescription = agent.name,
                tint = if (isSelected) NeonPurple else (if (agent.isActive) NeonPurple else TextTertiary),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        
        // Active indicator + name
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!agent.isActive) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(TextTertiary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                )
            }
            BasicText(
                text = agent.name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) NeonPurple else (if (agent.isActive) TextPrimary else TextTertiary),
                textAlign = Alignment.Center
            )
        }
        
        // System prompt preview or model info
        BasicText(
            text = if (agent.systemPrompt.isNotBlank()) agent.systemPrompt.take(30) + "..." else "Model: ${agent.model}",
            fontSize = 9.sp,
            color = TextTertiary,
            textAlign = Alignment.Center,
            maxLines = 1
        )
        
        // Delete button (show on long press or when selected)
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = onDelete,
                    colors = TextButtonDefaults.textButtonColors(contentColor = NeonPurple.copy(alpha = 0.8f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonPurple, modifier = Modifier.size(14.dp))
                        Text("Delete", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateAgentBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var showSystemPrompt by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardDark
    ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(CardDark, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(TextTertiary.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )
                }
                
                Text(
                    "Create New Agent",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Agent Name", color = TextSecondary) },
                    placeholder = { Text("e.g., My Assistant", color = TextTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
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
                    keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() })
                )
                
                // System prompt toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("System Prompt (optional)", color = TextSecondary, fontSize = 14.sp)
                    Switch(
                        checked = showSystemPrompt,
                        onCheckedChange = { showSystemPrompt = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple, checkedTrackColor = NeonPurple.copy(alpha = 0.3f))
                    )
                }
                
                if (showSystemPrompt) {
                    OutlinedTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        label = { Text("System Prompt", color = TextSecondary) },
                        placeholder = { Text("Custom instructions for this agent...", color = TextTertiary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
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
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusRequester.requestFocus() }),
                        maxLines = 4
                    )
                }
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        )
                    ) {
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onCreate(name, systemPrompt)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (name.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Create", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
    }
}
