@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Remove
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
import com.maya.clone.ui.viewmodel.SettingsAccessibilityViewModel
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsAccessibilityScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsAccessibilityViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val talkBackEnabled by viewModel.talkBackEnabled.collectAsStateWithLifecycle(false)
    val magnificationEnabled by viewModel.magnificationEnabled.collectAsStateWithLifecycle(false)
    val highContrastEnabled by viewModel.highContrastEnabled.collectAsStateWithLifecycle(false)
    val largeTextEnabled by viewModel.largeTextEnabled.collectAsStateWithLifecycle(false)
    val reduceMotionEnabled by viewModel.reduceMotionEnabled.collectAsStateWithLifecycle(false)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Accessibility", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Accessibility Services")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading accessibility settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "TalkBack",
                subtitle = "Screen reader for spoken feedback",
                leadingIcon = { Icon(Icons.Default.Accessibility, contentDescription = null, tint = NeonPurple) },
                checked = talkBackEnabled,
                onCheckedChange = { viewModel.setTalkBackEnabled(it) }
            )
            SettingsToggle(
                label = "Magnification",
                subtitle = "Zoom in on screen content",
                leadingIcon = { Icon(Icons.Default.ZoomIn, contentDescription = null, tint = NeonPurple) },
                checked = magnificationEnabled,
                onCheckedChange = { viewModel.setMagnificationEnabled(it) }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Display")

            SettingsToggle(
                label = "High Contrast",
                subtitle = "Increase color contrast",
                leadingIcon = { Icon(Icons.Default.Contrast, contentDescription = null, tint = NeonPurple) },
                checked = highContrastEnabled,
                onCheckedChange = { viewModel.setHighContrastEnabled(it) }
            )
            SettingsToggle(
                label = "Large Text",
                subtitle = "Increase font size across app",
                leadingIcon = { Icon(Icons.Default.TextIncrease, contentDescription = null, tint = NeonPurple) },
                checked = largeTextEnabled,
                onCheckedChange = { viewModel.setLargeTextEnabled(it) }
            )
            SettingsToggle(
                label = "Reduce Motion",
                subtitle = "Minimize animations and transitions",
                leadingIcon = { Icon(Icons.Default.Remove, contentDescription = null, tint = NeonPurple) },
                checked = reduceMotionEnabled,
                onCheckedChange = { viewModel.setReduceMotionEnabled(it) }
            )
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