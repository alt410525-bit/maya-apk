@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.maya.clone.ui.viewmodel.SettingsApiKeysViewModel
import com.maya.clone.ui.components.SettingsRow
import androidx.compose.ui.text.font.FontFamily

@Composable
fun SettingsSecretScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsApiKeysViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val apiKeys by viewModel.apiKeys.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showAddKeyDialog by remember { mutableStateOf(false) }
    var newKeyProvider by remember { mutableStateOf("openai") }
    var newKeyValue by remember { mutableStateOf("") }
    var newKeyLabel by remember { mutableStateOf("") }
    var showKeyValue by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar with back arrow
        TopAppBar(
            title = { Text("API Keys", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error display
        error?.let { errorMsg ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Error: $errorMsg", color = Error, fontSize = 12.sp)
            }
        }

        // Section: API Keys
        SectionHeader(title = "API Keys")

        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading API keys...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (apiKeys.isEmpty()) {
            SettingsRow(
                label = "No API keys configured",
                subtitle = "Add an API key to enable AI features",
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple) },
                onClick = { showAddKeyDialog = true }
            )
        } else {
            apiKeys.forEach { apiKey ->
                ApiKeyRow(
                    apiKey = apiKey,
                    showValue = showKeyValue,
                    onToggleVisibility = { showKeyValue = !showKeyValue },
                    onDelete = { viewModel.deleteApiKey(apiKey.id) }
                )
            }
        }

        // Add new key button
        SettingsRow(
            label = "Add API Key",
            subtitle = "Configure OpenAI, Anthropic, ElevenLabs, etc.",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = {
                newKeyProvider = "openai"
                newKeyValue = ""
                newKeyLabel = ""
                showAddKeyDialog = true
            }
        )

        // Add API Key Dialog
        if (showAddKeyDialog) {
            AddApiKeyDialog(
                onDismiss = {
                    showAddKeyDialog = false
                    newKeyValue = ""
                    newKeyLabel = ""
                },
                onCreate = { provider, key, label ->
                    showAddKeyDialog = false
                    viewModel.addApiKey(provider, key, label)
                    newKeyValue = ""
                    newKeyLabel = ""
                },
                provider = newKeyProvider,
                onProviderChange = { newKeyProvider = it },
                keyValue = newKeyValue,
                onKeyValueChange = { newKeyValue = it },
                label = newKeyLabel,
                onLabelChange = { newKeyLabel = it }
            )
        }
    }
}

@Composable
private fun ApiKeyRow(
    apiKey: com.maya.clone.data.entity.ApiKeyEntity,
    showValue: Boolean,
    onToggleVisibility: () -> Unit,
    onDelete: () -> Unit
) {
    val displayValue = if (showValue) {
        // In real app, decrypt the key. For now show masked with reveal option
        "sk-****${apiKey.id.takeLast(4)}"
    } else {
        "••••••••••••"
    }

    SettingsRow(
        label = apiKey.provider.uppercase(),
        subtitle = apiKey.label?.takeIf { it.isNotBlank() } ?: "API Key",
        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        if (showValue) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        tint = TextTertiary,
                        contentDescription = if (showValue) "Hide key" else "Show key"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, tint = Error, contentDescription = "Delete ${apiKey.provider} key")
                }
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                displayValue,
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Updated ${formatTimestamp(apiKey.updatedAt)}",
                color = TextTertiary,
                fontSize = 10.sp
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = (System.currentTimeMillis() - timestamp) / 1000
    return when {
        diff < 60 -> "just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86400 -> "${diff / 3600}h ago"
        else -> "${diff / 86400}d ago"
    }
}

@Composable
private fun AddApiKeyDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String?) -> Unit,
    provider: String,
    onProviderChange: (String) -> Unit,
    keyValue: String,
    onKeyValueChange: (String) -> Unit,
    label: String,
    onLabelChange: (String) -> Unit
) {
    val providers = listOf("openai", "anthropic", "elevenlabs", "google", "custom")
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add API Key", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Provider dropdown
                androidx.compose.material3.OutlinedTextField(
                    value = provider,
                    onValueChange = onProviderChange,
                    label = { Text("Provider", color = TextSecondary) },
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
                    androidx.compose.material3.Text(provider.uppercase(), color = TextPrimary)
                }

                // API Key value
                androidx.compose.material3.TextField(
                    value = keyValue,
                    onValueChange = onKeyValueChange,
                    label = { Text("API Key", color = TextSecondary) },
                    placeholder = { Text("sk-...", color = TextTertiary) },
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
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onNext = { focusRequester.requestFocus() }
                    ),
                    singleLine = true
                )

                // Label (optional)
                androidx.compose.material3.TextField(
                    value = label,
                    onValueChange = onLabelChange,
                    label = { Text("Label (optional)", color = TextSecondary) },
                    placeholder = { Text("e.g., Main OpenAI Key", color = TextTertiary) },
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
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    if (keyValue.isNotBlank()) {
                        onCreate(provider, keyValue, label.takeIf { it.isNotBlank() })
                    }
                },
                enabled = keyValue.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (keyValue.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
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
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(
            text = title,
            color = accentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Divider(color = DividerColor, thickness = 1.dp)
    }
}