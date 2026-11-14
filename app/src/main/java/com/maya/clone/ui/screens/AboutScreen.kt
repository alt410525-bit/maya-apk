@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.components.SettingsRow
import androidx.compose.foundation.shape.CircleShape

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("About Maya", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App icon and name
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(NeonPurple.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(50.dp))
                }
                Text("Maya AI Assistant", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Version 2.0.0", color = TextSecondary, fontSize = 14.sp)
                Text("Your intelligent companion", color = TextTertiary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        SectionHeader(title = "App Info")

        SettingsRow(
            label = "Version",
            subtitle = "2.0.0 (build 1)",
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = NeonPurple) }
        )
        SettingsRow(
            label = "Build Date",
            subtitle = "November 2026",
            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NeonPurple) }
        )
        SettingsRow(
            label = "Platform",
            subtitle = "Android (API 26+)",
            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = NeonPurple) }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Links
        SectionHeader(title = "Links")

        SettingsRow(
            label = "Website",
            subtitle = "maya-ai.example.com",
            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open website */ }
        )
        SettingsRow(
            label = "Support",
            subtitle = "support@maya-ai.example.com",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open email */ }
        )
        SettingsRow(
            label = "GitHub",
            subtitle = "github.com/your-org/maya-clone",
            leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open github */ }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Credits
        SectionHeader(title = "Credits")

        SettingsRow(
            label = "Open Source Libraries",
            subtitle = "Room, Hilt, Compose, ML Kit, Supabase, Coil...",
            leadingIcon = { Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = NeonPurple) }
        )
        SettingsRow(
            label = "Icons",
            subtitle = "Material Design Icons",
            leadingIcon = { Icon(Icons.Default.Extension, contentDescription = null, tint = NeonPurple) }
        )
        SettingsRow(
            label = "Fonts",
            subtitle = "System fonts (Roboto)",
            leadingIcon = { Icon(Icons.Default.FontDownload, contentDescription = null, tint = NeonPurple) }
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