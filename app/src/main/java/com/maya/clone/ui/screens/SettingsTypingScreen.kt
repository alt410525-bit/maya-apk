@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
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
import com.maya.clone.ui.viewmodel.SettingsTypingViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle

@Composable
fun SettingsTypingScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsTypingViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val showTypingIndicator by viewModel.showTypingIndicator.collectAsStateWithLifecycle(true)
    val keyboardSounds by viewModel.keyboardSounds.collectAsStateWithLifecycle(true)
    val autoCapitalize by viewModel.autoCapitalize.collectAsStateWithLifecycle(true)
    val autoCorrect by viewModel.autoCorrect.collectAsStateWithLifecycle(true)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Typing & Input", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Keyboard")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading typing settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            SettingsToggle(
                label = "Show Typing Indicator",
                subtitle = "Show when Maya is typing",
                checked = showTypingIndicator,
                onCheckedChange = { viewModel.setShowTypingIndicator(it) }
            )
            SettingsToggle(
                label = "Keyboard Sounds",
                subtitle = "Play sounds while typing",
                checked = keyboardSounds,
                onCheckedChange = { viewModel.setKeyboardSounds(it) }
            )
            SettingsToggle(
                label = "Auto-Capitalize",
                subtitle = "Automatically capitalize first letter",
                checked = autoCapitalize,
                onCheckedChange = { viewModel.setAutoCapitalize(it) }
            )
            SettingsToggle(
                label = "Auto-Correct",
                subtitle = "Automatically correct typos",
                checked = autoCorrect,
                onCheckedChange = { viewModel.setAutoCorrect(it) }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Voice Input")

            SettingsToggle(
                label = "Voice Input",
                subtitle = "Speak to type instead of keyboard",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsRow(
                label = "Language",
                subtitle = "English",
                trailing = { Text("English", color = TextSecondary, fontSize = 14.sp) }
            )
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(title = "Predictions")

            SettingsToggle(
                label = "Next-Word Predictions",
                subtitle = "Predict the next word you'll type",
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
            SettingsToggle(
                label = "Emoji Suggestions",
                subtitle = "Suggest emojis based on text",
                checked = true,
                onCheckedChange = { /* TODO */ }
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