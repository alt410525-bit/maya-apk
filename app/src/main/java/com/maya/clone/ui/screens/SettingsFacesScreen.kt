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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
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
import com.maya.clone.ui.viewmodel.SettingsFacesViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun SettingsFacesScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsFacesViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val faceProfiles by viewModel.faceProfiles.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Faces", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Face Profiles (${faceProfiles.size})")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading faces...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (faceProfiles.isEmpty()) {
            SettingsRow(
                label = "No faces registered",
                subtitle = "Add a face to enable Face Unlock",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonPurple) },
                onClick = { onNavigate(Navigation.SETTINGS_FACES_CAPTURE) }
            )
        } else {
            faceProfiles.forEach { profile ->
                SettingsRow(
                    label = profile.name,
                    subtitle = "Quality: ${(profile.qualityScore ?: 0f * 100).toInt()}% • Last updated: ${profile.updatedAt}",
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonPurple) },
                    trailing = {
                        IconButton(onClick = { viewModel.deleteFaceProfile(profile.id) }) {
                            Icon(Icons.Default.Delete, tint = Error, contentDescription = "Delete ${profile.name}")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add new face
        SettingsRow(
            label = "Add Face",
            subtitle = "Register a new face for unlock",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = { onNavigate(Navigation.SETTINGS_FACES_CAPTURE) }
        )
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}