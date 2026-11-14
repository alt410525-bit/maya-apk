@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Security
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
import com.maya.clone.ui.viewmodel.SettingsFaceLockViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle
import com.maya.clone.ui.components.SettingsSlider

@Composable
fun SettingsFaceLockScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsFaceLockViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val faceProfiles by viewModel.faceProfiles.collectAsStateWithLifecycle(emptyList())
    val recentAttempts by viewModel.recentAttempts.collectAsStateWithLifecycle(emptyList())
    val failureCount by viewModel.failureCount.collectAsStateWithLifecycle(0)
    val isFaceUnlockEnabled by viewModel.isFaceUnlockEnabled.collectAsStateWithLifecycle(false)
    val confidenceThreshold by viewModel.confidenceThreshold.collectAsStateWithLifecycle(0.7f)
    val pinFallbackEnabled by viewModel.pinFallbackEnabled.collectAsStateWithLifecycle(true)
    val patternFallbackEnabled by viewModel.patternFallbackEnabled.collectAsStateWithLifecycle(false)
    val isEnrolling by viewModel.isEnrolling.collectAsStateWithLifecycle(false)
    val enrollProgress by viewModel.enrollProgress.collectAsStateWithLifecycle(0)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showAddFaceDialog by remember { mutableStateOf(false) }
    var newFaceName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar with back arrow
        TopAppBar(
            title = { Text("Face Lock", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        // Section: Face Unlock
        SectionHeader(title = "Face Unlock")

        SettingsToggle(
            label = "Face Unlock",
            subtitle = "Unlock Maya with your face",
            checked = isFaceUnlockEnabled,
            onCheckedChange = { viewModel.setFaceUnlockEnabled(it) }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // Confidence slider
        SettingsSlider(
            label = "Confidence Threshold",
            value = confidenceThreshold,
            onValueChange = { viewModel.setConfidenceThreshold(it) },
            valueRange = 0.5f..0.95f,
            steps = 9,
            valueLabel = "${(confidenceThreshold * 100).toInt()}%"
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // Fallback options
        SectionHeader(title = "Fallback Options")

        SettingsToggle(
            label = "PIN Fallback",
            subtitle = "Use PIN if face recognition fails",
            checked = pinFallbackEnabled,
            onCheckedChange = { viewModel.setPinFallbackEnabled(it) }
        )
        SettingsToggle(
            label = "Pattern Fallback",
            subtitle = "Use pattern if face recognition fails",
            checked = patternFallbackEnabled,
            onCheckedChange = { viewModel.setPatternFallbackEnabled(it) }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        // Trusted faces
        SectionHeader(title = "Trusted Faces (${faceProfiles.size})")

        if (faceProfiles.isEmpty()) {
            SettingsRow(
                label = "No faces registered",
                subtitle = "Add a face to enable Face Unlock",
                leadingIcon = { Icon(Icons.Default.Face, contentDescription = null, tint = NeonPurple) },
                onClick = {
                    newFaceName = ""
                    showAddFaceDialog = true
                }
            )
        } else {
            faceProfiles.forEach { profile ->
                SettingsRow(
                    label = profile.name,
                    subtitle = "Quality: ${(profile.qualityScore ?: 0f * 100).toInt()}%",
                    leadingIcon = { Icon(Icons.Default.Face, contentDescription = null, tint = NeonPurple) },
                    trailing = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (profile.qualityScore != null) {
                                Text(
                                    "${(profile.qualityScore!! * 100).toInt()}%",
                                    color = if (profile.qualityScore!! >= 0.8f) Success else Warning,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteFaceProfile(profile.id) }
                            ) {
                                Icon(Icons.Default.Delete, tint = Error, contentDescription = "Delete ${profile.name}")
                            }
                        }
                    },
                    onClick = { /* Could show face details */ }
                )
            }
        }

        // Add face button
        SettingsRow(
            label = "Add Face",
            subtitle = "Register a new face for unlock",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = {
                newFaceName = ""
                showAddFaceDialog = true
            }
        )

        // Enrollment progress dialog
        if (isEnrolling) {
            EnrollmentProgressDialog(
                progress = enrollProgress,
                faceName = newFaceName
            )
        }

        // Add face dialog
        if (showAddFaceDialog) {
            AddFaceDialog(
                onDismiss = { showAddFaceDialog = false },
                onCreate = { name ->
                    showAddFaceDialog = false
                    viewModel.startEnrollment(name)
                },
                name = newFaceName,
                onNameChange = { newFaceName = it }
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
private fun AddFaceDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Face", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter a name for this face profile:", color = TextSecondary, fontSize = 14.sp)
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Face Name", color = TextSecondary) },
                    placeholder = { Text("e.g., My Face", color = TextTertiary) },
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
                        onCreate(name)
                    }
                },
                enabled = name.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Register", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
private fun EnrollmentProgressDialog(progress: Int, faceName: String) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { /* Can't dismiss during enrollment */ },
        title = { Text("Registering Face: $faceName", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(80.dp).clip(androidx.compose.ui.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.size(60.dp),
                        color = NeonPurple,
                        strokeWidth = 6.dp,
                        trackColor = NeonPurple.copy(alpha = 0.2f)
                    )
                }
                Text("$progress%", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("Look at the camera...", color = TextSecondary, fontSize = 14.sp)
                Text("Move your head slightly for better coverage", color = TextTertiary, fontSize = 12.sp)
            }
        }
    )
}