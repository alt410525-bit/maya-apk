@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
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
import com.maya.clone.ui.viewmodel.SettingsSocialViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle
import androidx.compose.ui.text.font.FontFamily

@Composable
fun SettingsSocialScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsSocialViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val shareOnTwitter by viewModel.shareOnTwitter.collectAsStateWithLifecycle(false)
    val shareOnFacebook by viewModel.shareOnFacebook.collectAsStateWithLifecycle(false)
    val shareOnInstagram by viewModel.shareOnInstagram.collectAsStateWithLifecycle(false)
    val referralCode by viewModel.referralCode.collectAsStateWithLifecycle("MAYA-REF-001")
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Social & Calls", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Contacts")

        SettingsToggle(
            label = "Show Contact Status",
            subtitle = "Display online status of contacts",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Sync Contacts",
            subtitle = "Import and sync phone contacts",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Calling")

        SettingsToggle(
            label = "Voice Calls",
            subtitle = "Enable/disable voice calls",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Video Calls",
            subtitle = "Enable/disable video calls",
            checked = true,
            onCheckedChange = { /* TODO */ }
        )
        SettingsToggle(
            label = "Call Recording",
            subtitle = "Record incoming/outgoing calls",
            checked = false,
            onCheckedChange = { /* TODO */ }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Sharing")

        SettingsRow(
            label = "Share Profile",
            subtitle = "Share your Maya profile with friends",
            leadingIcon = { Icon(Icons.Default.ContactPhone, contentDescription = null, tint = NeonCyan) },
            onClick = { /* TODO: share profile */ }
        )
        SettingsRow(
            label = "Share Media",
            subtitle = "Share photos and files",
            leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = NeonCyan) },
            onClick = { /* TODO: share media */ }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Social Sharing")

        SettingsToggle(
            label = "Share on Twitter",
            subtitle = "Post Maya responses to Twitter",
            checked = shareOnTwitter,
            onCheckedChange = { viewModel.setShareOnTwitter(it) }
        )
        SettingsToggle(
            label = "Share on Facebook",
            subtitle = "Post Maya responses to Facebook",
            checked = shareOnFacebook,
            onCheckedChange = { viewModel.setShareOnFacebook(it) }
        )
        SettingsToggle(
            label = "Share on Instagram",
            subtitle = "Post Maya responses to Instagram",
            checked = shareOnInstagram,
            onCheckedChange = { viewModel.setShareOnInstagram(it) }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "Referral Program")

        SettingsRow(
            label = "Your Referral Code",
            subtitle = "Share to earn credits",
            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = NeonPurple) },
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(referralCode, color = NeonPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Icon(Icons.Default.ContentCopy, tint = TextTertiary, contentDescription = "Copy code")
                }
            }
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