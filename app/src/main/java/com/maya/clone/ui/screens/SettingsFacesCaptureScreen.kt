@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
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
import com.maya.clone.ui.viewmodel.SettingsFacesCaptureViewModel

@Composable
fun SettingsFacesCaptureScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsFacesCaptureViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val isEnrolling by viewModel.isEnrolling.collectAsStateWithLifecycle(false)
    val enrollProgress by viewModel.enrollProgress.collectAsStateWithLifecycle(0)
    val enrollMessage by viewModel.enrollMessage.collectAsStateWithLifecycle("Position your face in the frame")
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    var showNameDialog by remember { mutableStateOf(false) }
    var faceName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Capture Face", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Camera Preview")

        if (isEnrolling) {
            // Enrollment progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark)
                    .clip(RoundedCornerShape(16.dp))
                    .height(200.dp)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(120.dp).clip(androidx.compose.ui.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            progress = enrollProgress / 100f,
                            modifier = Modifier.size(100.dp),
                            color = NeonPurple,
                            strokeWidth = 8.dp,
                            trackColor = NeonPurple.copy(alpha = 0.2f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(enrollProgress.toString() + "%", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(enrollMessage, color = TextSecondary, fontSize = 16.sp)
                }
            }
        } else {
            // Camera preview placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark)
                    .clip(RoundedCornerShape(16.dp))
                    .height(200.dp)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .contentAlignment(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Camera, tint = NeonPurple, contentDescription = "Camera preview", modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Preview will appear here",
                            color = TextTertiary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!isEnrolling) {
            // Capture/Retake buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark)
                    .clip(RoundedCornerShape(24.dp))
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spaceEvenly
                ) {
                    // Capture button
                    OutlinedButton(
                        onClick = {
                            faceName = ""
                            showNameDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Capture Face", color = NeonPurple, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // Retake button (disabled if no previous capture)
                    OutlinedButton(
                        onClick = { /* TODO: retake */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retake", color = TextTertiary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Name dialog
        if (showNameDialog) {
            AddFaceNameDialog(
                onDismiss = { showNameDialog = false },
                onConfirm = { name ->
                    showNameDialog = false
                    viewModel.startEnrollment(name)
                },
                name = faceName,
                onNameChange = { faceName = it }
            )
        }
    }
}

@Composable
private fun AddFaceNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit
) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Face", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).width(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter a name for this face:", color = TextSecondary, fontSize = 14.sp)
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
                        onConfirm(name)
                    }
                },
                enabled = name.isNotBlank(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (name.isNotBlank()) NeonPurple else NeonPurple.copy(alpha = 0.3f),
                    contentColor = Color.Black
                )
            ) {
                Text("Start Capture", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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