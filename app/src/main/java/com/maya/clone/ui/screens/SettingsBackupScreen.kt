@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
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
import com.maya.clone.ui.viewmodel.SettingsBackupViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsBackupScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsBackupViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val backupJobs by viewModel.backupJobs.collectAsStateWithLifecycle(emptyList())
    val pendingJobs by viewModel.pendingJobs.collectAsStateWithLifecycle(emptyList())
    val isCreating by viewModel.isCreating.collectAsStateWithLifecycle(false)
    val createProgress by viewModel.createProgress.collectAsStateWithLifecycle(0)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar with back arrow
        TopAppBar(
            title = { Text("Backup & Restore", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        // Section: Auto Backup
        SectionHeader(title = "Auto Backup")

        SettingsToggle(
            label = "Auto Backup",
            subtitle = "Daily at 2:00 AM",
            checked = true,
            onCheckedChange = { /* TODO: wire to settings */ }
        )
        SettingsRow(
            label = "Include Media",
            subtitle = "Photos and voice messages",
            trailing = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enabled", color = Success, fontSize = 14.sp)
                    Icon(Icons.Default.Check, contentDescription = null, tint = Success)
                }
            }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Section: Manual Backup
        SectionHeader(title = "Manual Backup")

        if (isCreating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Creating Backup...", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("$createProgress%", color = NeonPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = createProgress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = NeonPurple,
                        trackColor = NeonPurple.copy(alpha = 0.2f)
                    )
                    Text("Encrypting and uploading...", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            SettingsRow(
                label = "Create Full Backup",
                subtitle = "Encrypt and upload to cloud",
                leadingIcon = { Icon(Icons.Default.CloudUpload, contentDescription = null, tint = NeonPurple) },
                onClick = { viewModel.createBackup("full") }
            )
            SettingsRow(
                label = "Create Incremental Backup",
                subtitle = "Only changes since last backup",
                leadingIcon = { Icon(Icons.Default.CloudUpload, contentDescription = null, tint = NeonCyan) },
                onClick = { viewModel.createBackup("incremental") }
            )
        }

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Backup History
        SectionHeader(title = "Backup History (${backupJobs.size})")

        if (backupJobs.isEmpty()) {
            SettingsRow(
                label = "No backups yet",
                subtitle = "Create your first backup above",
                leadingIcon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = NeonPurple) },
                onClick = { viewModel.createBackup("full") }
            )
        } else {
            backupJobs.forEach { job ->
                BackupJobRow(job = job, onRestore = { viewModel.restoreBackup(job.id) })
            }
        }

        // Actions
        SectionHeader(title = "Actions")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { viewModel.createBackup("full") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Backup Now", color = TextPrimary, fontSize = 14.sp)
            }
            OutlinedButton(
                onClick = { /* navigate to restore */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Restore", color = TextPrimary, fontSize = 14.sp)
            }
        }

        OutlinedButton(
            onClick = { /* show delete confirmation */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Delete All Data", color = Error, fontSize = 14.sp)
        }
    }
}

@Composable
private fun BackupJobRow(
    job: com.maya.clone.data.entity.BackupJobEntity,
    onRestore: () -> Unit
) {
    val statusColor = when (job.status) {
        "completed" -> Success
        "running" -> NeonCyan
        "failed" -> Error
        else -> Warning
    }

    SettingsRow(
        label = job.type.capitalize(),
        subtitle = "${job.sizeBytes?.let { formatBytes(it) } ?: "Unknown size"} • ${formatTimestamp(job.createdAt)}",
        leadingIcon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = statusColor) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(job.status.capitalize(), color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                if (job.status == "completed") {
                    OutlinedButton(
                        onClick = onRestore,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("Restore", fontSize = 11.sp)
                    }
                }
            }
        }
    ) {
        if (job.status == "failed") {
            Text(
                "Error: ${job.errorMessage ?: "Unknown error"}",
                color = Error,
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024 * 1024)} GB"
        bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        bytes >= 1024 -> "${bytes / 1024} KB"
        else -> "$bytes B"
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