@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
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
import com.maya.clone.ui.viewmodel.SettingsSOSViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsSOSScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsSOSViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val emergencyContacts by viewModel.emergencyContacts.collectAsStateWithLifecycle(emptyList())
    val autoSendLocation by viewModel.autoSendLocation.collectAsStateWithLifecycle(true)
    val autoCallEmergency by viewModel.autoCallEmergency.collectAsStateWithLifecycle(false)
    val messageTemplate by viewModel.messageTemplate.collectAsStateWithLifecycle("Emergency! I need help. My location: {location}")
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    var newContactRole by remember { mutableStateOf("Emergency") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("SOS Emergency", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Emergency Contacts (${emergencyContacts.size})")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading SOS settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (emergencyContacts.isEmpty()) {
            SettingsRow(
                label = "No emergency contacts",
                subtitle = "Add contacts who will be alerted in an emergency",
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newContactName = ""
                    newContactPhone = ""
                    newContactRole = "Emergency"
                    showAddContactDialog = true
                }
            )
        } else {
            emergencyContacts.forEach { contact ->
                EmergencyContactRow(
                    contact = contact,
                    onRemove = { viewModel.removeContact(contact) }
                )
            }
        }

        // Add contact button
        SettingsRow(
            label = "Add Emergency Contact",
            subtitle = "Add a new contact for emergency alerts",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = {
                newContactName = ""
                newContactPhone = ""
                newContactRole = "Emergency"
                showAddContactDialog = true
            }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // SOS Settings
        SectionHeader(title = "SOS Settings")

        SettingsToggle(
            label = "Auto-Send Location",
            subtitle = "Include GPS location with emergency alerts",
            checked = autoSendLocation,
            onCheckedChange = { viewModel.setAutoSendLocation(it) }
        )
        SettingsToggle(
            label = "Auto-Call Emergency Services",
            subtitle = "Automatically dial emergency number on SOS trigger",
            checked = autoCallEmergency,
            onCheckedChange = { viewModel.setAutoCallEmergency(it) }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Message Template
        SectionHeader(title = "Alert Message Template")

        SettingsRow(
            label = "Message Template",
            subtitle = messageTemplate,
            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: edit template dialog */ }
        )

        // Add contact dialog
        if (showAddContactDialog) {
            AddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onAdd = { name, phone, role ->
                    showAddContactDialog = false
                    viewModel.addContact("$name:$phone:$role")
                },
                name = newContactName,
                onNameChange = { newContactName = it },
                phone = newContactPhone,
                onPhoneChange = { newContactPhone = it },
                role = newContactRole,
                onRoleChange = { newContactRole = it }
            )
        }
    }
}

@Composable
private fun EmergencyContactRow(
    contact: String,
    onRemove: () -> Unit
) {
    // Parse contact format: "name:phone:role"
    val parts = contact.split(":")
    val name = parts.getOrNull(0) ?: ""
    val phone = parts.getOrNull(1) ?: ""
    val role = parts.getOrNull(2) ?: ""

    SettingsRow(
        label = name,
        subtitle = "$role • $phone",
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonPurple) },
        trailing = {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, tint = Error, contentDescription = "Remove $name")
            }
        }
    )
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    role: String,
    onRoleChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emergency Contact", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name", color = TextSecondary) },
                    placeholder = { Text("e.g., John Doe", color = TextTertiary) },
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
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number", color = TextSecondary) },
                    placeholder = { Text("+1 555 123 4567", color = TextTertiary) },
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
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onNext = { focusRequester.requestFocus() }
                    ),
                    singleLine = true
                )

                androidx.compose.material3.TextField(
                    value = role,
                    onValueChange = onRoleChange,
                    label = { Text("Role", color = TextSecondary) },
                    placeholder = { Text("e.g., Emergency, Family, Doctor", color = TextTertiary) },
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
                        onDone = { focusRequester.requestFocus() }
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, phone, role)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank() && phone.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
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