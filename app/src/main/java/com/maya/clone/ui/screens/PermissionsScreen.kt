@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
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
import com.maya.clone.ui.viewmodel.PermissionsViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun PermissionsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PermissionsViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val cameraPermission by viewModel.cameraPermission.collectAsStateWithLifecycle(false)
    val microphonePermission by viewModel.microphonePermission.collectAsStateWithLifecycle(false)
    val locationPermission by viewModel.locationPermission.collectAsStateWithLifecycle(false)
    val storagePermission by viewModel.storagePermission.collectAsStateWithLifecycle(false)
    val contactsPermission by viewModel.contactsPermission.collectAsStateWithLifecycle(false)
    val phonePermission by viewModel.phonePermission.collectAsStateWithLifecycle(false)
    val smsPermission by viewModel.smsPermission.collectAsStateWithLifecycle(false)
    val notificationPermission by viewModel.notificationPermission.collectAsStateWithLifecycle(false)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Permissions", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "App Permissions")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading permissions...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            val permissions = listOf(
                "Camera" to Icons.Default.Camera to cameraPermission to { viewModel.setCameraPermission(it) },
                "Microphone" to Icons.Default.Mic to microphonePermission to { viewModel.setMicrophonePermission(it) },
                "Location" to Icons.Default.LocationOn to locationPermission to { viewModel.setLocationPermission(it) },
                "Storage" to Icons.Default.Folder to storagePermission to { viewModel.setStoragePermission(it) },
                "Contacts" to Icons.Default.Person to contactsPermission to { viewModel.setContactsPermission(it) },
                "Phone" to Icons.Default.Phone to phonePermission to { viewModel.setPhonePermission(it) },
                "SMS" to Icons.Default.Message to smsPermission to { viewModel.setSmsPermission(it) },
                "Notifications" to Icons.Default.Notifications to notificationPermission to { viewModel.setNotificationPermission(it) }
            )

            permissions.forEach { (name, icon, granted, onChange) ->
                SettingsRow(
                    label = name,
                    subtitle = if (granted) "Allowed" else "Not allowed",
                    leadingIcon = { Icon(icon, contentDescription = null, tint = if (granted) Success else NeonPurple) },
                    trailing = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (granted) "Allowed" else "Denied",
                                color = if (granted) Success else Error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                if (granted) Icons.Default.CheckCircle else Icons.Default.Cancel, contentDescription = null,
                                tint = if (granted) Success else Error
                            )
                        }
                    },
                    onClick = { onChange(!granted) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}