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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
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
import com.maya.clone.ui.viewmodel.SettingsGuardianViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsGuardianScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsGuardianViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val contentFilter by viewModel.contentFilter.collectAsStateWithLifecycle(true)
    val parentalControls by viewModel.parentalControls.collectAsStateWithLifecycle(false)
    val screenTimeLimit by viewModel.screenTimeLimit.collectAsStateWithLifecycle(120)
    val blockedKeywords by viewModel.blockedKeywords.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    var showAddKeywordDialog by remember { mutableStateOf(false) }
    var newKeyword by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Guardian", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Guardian Mode")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading guardian settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Content Filter",
                subtitle = "Filter inappropriate content",
                checked = contentFilter,
                onCheckedChange = { viewModel.setContentFilter(it) }
            )
            SettingsToggle(
                label = "Parental Controls",
                subtitle = "Enable guardian mode for supervised use",
                checked = parentalControls,
                onCheckedChange = { viewModel.setParentalControls(it) }
            )
            SettingsRow(
                label = "Screen Time Limit",
                subtitle = "$screenTimeLimit minutes per day",
                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: number picker */ }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Alerts & Actions")

            SettingsToggle(
                label = "Emergency Alerts",
                subtitle = "Send alerts to guardian contacts",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Location Sharing",
                subtitle = "Share location when alert triggered",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Auto-Reply to Guardian",
                subtitle = "Auto-respond to guardian messages",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Blocked Keywords (${blockedKeywords.size})")

            if (blockedKeywords.isEmpty()) {
                SettingsRow(
                    label = "No blocked keywords",
                    subtitle = "Add keywords to filter",
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                    onClick = {
                        newKeyword = ""
                        showAddKeywordDialog = true
                    }
                )
            } else {
                blockedKeywords.forEach { keyword ->
                    SettingsRow(
                        label = keyword,
                        subtitle = "Blocked",
                        leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = NeonPurple) },
                        trailing = {
                            IconButton(onClick = { viewModel.removeBlockedKeyword(keyword) }) {
                                Icon(Icons.Default.Delete, tint = Error, contentDescription = "Remove $keyword")
                            }
                        }
                    )
                }
            }

            // Add keyword button
            SettingsRow(
                label = "Add Keyword",
                subtitle = "Block a new keyword",
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newKeyword = ""
                    showAddKeywordDialog = true
                }
            )

            // Add keyword dialog
            if (showAddKeywordDialog) {
                AddKeywordDialog(
                    onDismiss = { showAddKeywordDialog = false },
                    onAdd = { keyword ->
                        showAddKeywordDialog = false
                        viewModel.addBlockedKeyword(keyword)
                    },
                    keyword = newKeyword,
                    onKeywordChange = { newKeyword = it }
                )
            }
        }
    }
}

@Composable
private fun AddKeywordDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    keyword: String,
    onKeywordChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Block Keyword", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = keyword,
                    onValueChange = onKeywordChange,
                    label = { Text("Keyword", color = TextSecondary) },
                    placeholder = { Text("e.g., inappropriate", color = TextTertiary) },
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
                    if (keyword.isNotBlank()) {
                        onAdd(keyword)
                    }
                },
                enabled = keyword.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (keyword.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Block", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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