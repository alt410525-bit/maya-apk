@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.maya.clone.ui.viewmodel.SettingsThemeViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun SettingsThemeScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsThemeViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle("dark")
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Theme", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Appearance Mode")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading theme settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeModeCard(
                    title = "Dark",
                    isSelected = selectedTheme == "dark",
                    onSelect = { viewModel.setTheme("dark") }
                )
                ThemeModeCard(
                    title = "Light",
                    isSelected = selectedTheme == "light",
                    onSelect = { viewModel.setTheme("light") }
                )
                ThemeModeCard(
                    title = "System",
                    isSelected = selectedTheme == "system",
                    onSelect = { viewModel.setTheme("system") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Accent Colour
        SectionHeader(title = "Accent Colour")

        val accentColors = listOf(
            "Neon Purple" to NeonPurple,
            "Neon Cyan" to NeonCyan,
            "Neon Pink" to NeonPink,
            "Emerald" to Color(0xFF10B981),
            "Amber" to Color(0xFFF59E0B),
            "Rose" to Color(0xFFF43F5E),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            accentColors.forEach { (name, color) ->
                AccentColorCircle(
                    name = name,
                    color = color,
                    isSelected = selectedTheme == "dark", // Simplified - would check actual accent
                    onSelect = { /* TODO: set accent */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Wallpaper
        SectionHeader(title = "Wallpaper")

        SettingsRow(
            label = "Wallpaper",
            subtitle = "Select a background image",
            leadingIcon = { Icon(Icons.Default.Contrast, contentDescription = null, tint = NeonCyan) },
            onClick = { /* TODO: wallpaper picker */ }
        )
    }
}

@Composable
private fun ThemeModeCard(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgColor = if (isSelected) NeonPurple else CardDark
    val textColor = if (isSelected) Color.White else TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun AccentColorCircle(
    name: String,
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color, CircleShape)
                .clickable(onClick = onSelect)
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
        Text(name, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}