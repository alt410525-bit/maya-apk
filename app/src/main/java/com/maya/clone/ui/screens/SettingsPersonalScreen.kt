@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
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
import com.maya.clone.ui.viewmodel.SettingsPersonalViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun SettingsPersonalScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsPersonalViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val displayName by viewModel.displayName.collectAsStateWithLifecycle("")
    val avatarUrl by viewModel.avatarUrl.collectAsStateWithLifecycle(null)
    val email by viewModel.email.collectAsStateWithLifecycle("")
    val age by viewModel.age.collectAsStateWithLifecycle(null)
    val gender by viewModel.gender.collectAsStateWithLifecycle("")
    val timezone by viewModel.timezone.collectAsStateWithLifecycle("UTC")
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }

    var showEditAgeDialog by remember { mutableStateOf(false) }
    var editAge by remember { mutableStateOf("") }

    var showEditGenderDialog by remember { mutableStateOf(false) }
    var editGender by remember { mutableStateOf("") }

    var showEditTimezoneDialog by remember { mutableStateOf(false) }
    var editTimezone by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar with back arrow
        TopAppBar(
            title = { Text("Personal Info", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        // Loading
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading personal info...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            // Section: Profile
            SectionHeader(title = "Profile")

            // Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(androidx.compose.ui.shape.CircleShape)
                        .background(NeonPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(48.dp))
                }
            }

            // Name
            SettingsRow(
                label = "Display Name",
                subtitle = displayName.ifEmpty { "Not set" },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    editName = displayName
                    showEditNameDialog = true
                }
            )

            // Age
            SettingsRow(
                label = "Age",
                subtitle = age?.toString() ?: "Not set",
                leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    editAge = age?.toString() ?: ""
                    showEditAgeDialog = true
                }
            )

            // Gender
            SettingsRow(
                label = "Gender",
                subtitle = gender.ifEmpty { "Not set" },
                leadingIcon = { Icon(Icons.Default.Transgender, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    editGender = gender
                    showEditGenderDialog = true
                }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Section: Account
            SectionHeader(title = "Account")

            SettingsRow(
                label = "Email",
                subtitle = email.ifEmpty { "Not set" },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: Email change flow */ }
            )

            SettingsRow(
                label = "Timezone",
                subtitle = timezone,
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    editTimezone = timezone
                    showEditTimezoneDialog = true
                }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Section: Data Management
            SectionHeader(title = "Data Management")

            SettingsRow(
                label = "Export Data",
                subtitle = "Export all your data as JSON",
                leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null, tint = NeonCyan) },
                onClick = { onNavigate(Navigation.SETTINGS_EXPORT_DATA) }
            )

            SettingsRow(
                label = "Delete Account",
                subtitle = "Remove all data permanently",
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Error) },
                onClick = { /* TODO: Delete account confirmation */ }
            )
        }

        // Dialogs
        if (showEditNameDialog) {
            EditTextDialog(
                title = "Edit Display Name",
                value = editName,
                onValueChange = { editName = it },
                onConfirm = {
                    viewModel.saveDisplayName(editName)
                    showEditNameDialog = false
                },
                onDismiss = { showEditNameDialog = false },
                hint = "Enter your name"
            )
        }

        if (showEditAgeDialog) {
            EditNumberDialog(
                title = "Edit Age",
                value = editAge,
                onValueChange = { editAge = it },
                onConfirm = {
                    editAge.toIntOrNull()?.let { viewModel.saveAge(it) }
                    showEditAgeDialog = false
                },
                onDismiss = { showEditAgeDialog = false }
            )
        }

        if (showEditGenderDialog) {
            GenderSelectionDialog(
                title = "Edit Gender",
                currentValue = editGender,
                onValueChange = { editGender = it },
                onConfirm = {
                    viewModel.saveGender(editGender)
                    showEditGenderDialog = false
                },
                onDismiss = { showEditGenderDialog = false }
            )
        }

        if (showEditTimezoneDialog) {
            TimezoneSelectionDialog(
                title = "Edit Timezone",
                currentValue = editTimezone,
                onValueChange = { editTimezone = it },
                onConfirm = {
                    viewModel.saveTimezone(editTimezone)
                    showEditTimezoneDialog = false
                },
                onDismiss = { showEditTimezoneDialog = false }
            )
        }
    }
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
                    singleLine = true
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

@Composable
private fun EditNumberDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
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
                    placeholder = { Text("Enter value", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
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
                    singleLine = true
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

@Composable
private fun GenderSelectionDialog(
    title: String,
    currentValue: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf("Male", "Female", "Non-binary", "Prefer not to say", "Custom")

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .clickable { onValueChange(option) }
                            .background(if (option == currentValue) NeonPurple.copy(alpha = 0.1f) else Color.Transparent)
                    ) {
                        Text(option, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (option == currentValue) {
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
            androidx.compose.material3.Button(onClick = { onConfirm(); onDismiss() },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.Black)
            ) { Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun TimezoneSelectionDialog(
    title: String,
    currentValue: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val commonTimezones = listOf(
        "UTC", "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles",
        "America/Anchorage", "Pacific/Honolulu", "Europe/London", "Europe/Paris", "Europe/Berlin",
        "Europe/Moscow", "Asia/Dubai", "Asia/Kolkata", "Asia/Shanghai", "Asia/Tokyo",
        "Australia/Sydney", "Pacific/Auckland"
    )

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                commonTimezones.forEach { tz ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .clickable { onValueChange(tz) }
                            .background(if (tz == currentValue) NeonPurple.copy(alpha = 0.1f) else Color.Transparent)
                    ) {
                        Text(tz, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        if (tz == currentValue) {
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
            androidx.compose.material3.Button(onClick = { onConfirm(); onDismiss() },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.Black)
            ) { Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        }
    )
}