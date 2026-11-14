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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.maya.clone.ui.viewmodel.SettingsWhatsAppViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsWhatsAppGroupsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsWhatsAppViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle(false)
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle("")
    val groups by viewModel.groups.collectAsStateWithLifecycle(emptyList())
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showConnectDialog by remember { mutableStateOf(false) }
    var connectPhone by remember { mutableStateOf("") }
    var showAddGroupDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("WhatsApp Groups", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Connection")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading WhatsApp settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (!isConnected) {
            SettingsRow(
                label = "Not Connected",
                subtitle = "Scan QR code to link WhatsApp",
                leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    connectPhone = ""
                    showConnectDialog = true
                }
            )
        } else {
            SettingsRow(
                label = "Connected",
                subtitle = "Phone: $phoneNumber",
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success) },
                trailing = {
                    OutlinedButton(
                        onClick = { viewModel.disconnectWhatsApp() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Error
                        )
                    ) {
                        Text("Disconnect", fontSize = 12.sp)
                    }
                }
            )

            SettingsToggle(
                label = "Group Notifications",
                subtitle = "Receive notifications from linked groups",
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
            )
        }

        // Connect dialog
        if (showConnectDialog) {
            ConnectWhatsAppDialog(
                onDismiss = { showConnectDialog = false },
                onConnect = { phone ->
                    showConnectDialog = false
                    viewModel.connectWhatsApp(phone)
                },
                phone = connectPhone,
                onPhoneChange = { connectPhone = it }
            )
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Groups section
        SectionHeader(title = "Linked Groups (${groups.size})")

        if (groups.isEmpty() && isConnected) {
            SettingsRow(
                label = "No groups linked",
                subtitle = "Add a WhatsApp group to sync",
                leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newGroupName = ""
                    showAddGroupDialog = true
                }
            )
        } else {
            groups.forEach { group ->
                GroupRow(
                    groupName = group,
                    onRemove = { viewModel.removeGroup(group) }
                )
            }
        }

        // Add group button
        SettingsRow(
            label = "Add WhatsApp Group",
            subtitle = "Link a new group for sync",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = {
                newGroupName = ""
                showAddGroupDialog = true
            }
        )

        // Connect dialog
        if (showConnectDialog) {
            ConnectWhatsAppDialog(
                onDismiss = { showConnectDialog = false },
                onConnect = { phone ->
                    showConnectDialog = false
                    viewModel.connectWhatsApp(phone)
                },
                phone = connectPhone,
                onPhoneChange = { connectPhone = it }
            )
        }

        // Add group dialog
        if (showAddGroupDialog) {
            AddGroupDialog(
                onDismiss = { showAddGroupDialog = false },
                onAdd = { name ->
                    showAddGroupDialog = false
                    viewModel.addGroup(name)
                },
                name = newGroupName,
                onNameChange = { newGroupName = it }
            )
        }
    }
}

@Composable
private fun GroupRow(
    groupName: String,
    onRemove: () -> Unit
) {
    SettingsRow(
        label = groupName,
        subtitle = "Synced",
        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = NeonPurple) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Synced", color = Success, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, tint = Error, contentDescription = "Remove $groupName")
                }
            }
        }
    )
}

@Composable
private fun ConnectWhatsAppDialog(
    onDismiss: () -> Unit,
    onConnect: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect WhatsApp", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter your phone number with country code:", color = TextSecondary, fontSize = 14.sp)
                androidx.compose.material3.TextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number", color = TextSecondary) },
                    placeholder = { Text("+1 555 123 4567", color = TextTertiary) },
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
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
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
                    if (phone.isNotBlank()) {
                        onConnect(phone)
                    }
                },
                enabled = phone.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (phone.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Connect", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
private fun AddGroupDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add WhatsApp Group", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Group Name", color = TextSecondary) },
                    placeholder = { Text("e.g., Study Group", color = TextTertiary) },
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
                    if (name.isNotBlank()) {
                        onAdd(name)
                    }
                },
                enabled = name.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
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