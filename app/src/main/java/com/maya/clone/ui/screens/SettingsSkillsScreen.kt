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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
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
import com.maya.clone.ui.viewmodel.SettingsSkillsViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun SettingsSkillsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsSkillsViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val availableSkills by viewModel.availableSkills.collectAsStateWithLifecycle(emptyList())
    val installedSkills by viewModel.installedSkills.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Skills", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        SectionHeader(title = "Available Skills (${installedSkills.size} installed)")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading skills...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableSkills, key = { it.id }) { skill ->
                    SkillRow(
                        skill = skill,
                        isInstalled = skill.isInstalled,
                        onToggle = {
                            if (skill.isInstalled) {
                                viewModel.uninstallSkill(skill.id)
                            } else {
                                viewModel.installSkill(skill.id)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Installed skills summary
        if (installedSkills.isNotEmpty()) {
            SectionHeader(title = "Installed Skills")
            installedSkills.forEach { skillId ->
                availableSkills.find { it.id == skillId }?.let { skill ->
                    SkillSummaryRow(skill = skill)
                }
            }
        }
    }
}

@Composable
private fun SkillRow(
    skill: SettingsSkillsViewModel.SkillInfo,
    isInstalled: Boolean,
    onToggle: () -> Unit
) {
    val iconRes = when (skill.icon) {
        "search" -> Icons.Default.Search
        "code" -> Icons.Default.Code
        "file" -> Icons.Default.Folder
        "image" -> Icons.Default.Image
        "analytics" -> Icons.Default.Analytics
        "email" -> Icons.Default.Email
        "calendar" -> Icons.Default.CalendarMonth
        "translate" -> Icons.Default.Translate
        "ocr" -> Icons.Default.Lens
        "mic" -> Icons.Default.Mic
        "volume_up" -> Icons.Default.VolumeUp
        "spider" -> Icons.Default.Web
        else -> Icons.Default.Lightbulb
    }

    SettingsRow(
        label = skill.name,
        subtitle = skill.description,
        leadingIcon = { Icon(iconRes, contentDescription = null, tint = NeonPurple) },
        trailing = {
            androidx.compose.material3.Switch(
                checked = isInstalled,
                onCheckedChange = { _ -> onToggle() },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = NeonPurple,
                    checkedTrackColor = NeonPurple.copy(alpha = 0.3f)
                )
            )
        },
        onClick = { /* navigate to skill config */ }
    )
}

@Composable
private fun SkillSummaryRow(skill: SettingsSkillsViewModel.SkillInfo) {
    val iconRes = when (skill.icon) {
        "search" -> Icons.Default.Search
        "code" -> Icons.Default.Code
        "file" -> Icons.Default.Folder
        "image" -> Icons.Default.Image
        "analytics" -> Icons.Default.Analytics
        "email" -> Icons.Default.Email
        "calendar" -> Icons.Default.CalendarMonth
        "translate" -> Icons.Default.Translate
        "ocr" -> Icons.Default.Lens
        "mic" -> Icons.Default.Mic
        "volume_up" -> Icons.Default.VolumeUp
        "spider" -> Icons.Default.Web
        else -> Icons.Default.Lightbulb
    }

    SettingsRow(
        label = skill.name,
        subtitle = "Installed",
        leadingIcon = { Icon(iconRes, contentDescription = null, tint = Success) },
        trailing = {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success)
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