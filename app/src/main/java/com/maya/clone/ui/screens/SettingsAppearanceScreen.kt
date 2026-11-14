@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.SettingsAppearanceViewModel
import com.maya.clone.ui.components.SettingsRow
import com.maya.clone.ui.components.SettingsToggle
import com.maya.clone.ui.components.SettingsSlider

@Composable
fun SettingsAppearanceScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsAppearanceViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val themes by viewModel.themes.collectAsStateWithLifecycle(emptyList())
    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val fontScale by viewModel.fontScale.collectAsStateWithLifecycle(1.0f)
    val animationScale by viewModel.animationScale.collectAsStateWithLifecycle(1.0f)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar with back arrow
        TopAppBar(
            title = { Text("Appearance", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
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

        // Loading indicator
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading appearance settings...", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            // Section: Theme Selection
            SectionHeader(title = "Theme")

            themes.forEach { theme ->
                val isSelected = activeTheme?.id == theme.id
                SettingsRow(
                    label = theme.name,
                    subtitle = if (theme.isCustom) "Custom" else "Built-in",
                    leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, tint = NeonPurple) },
                    trailing = {
                        if (isSelected) {
                            Icon(Icons.Default.Check, tint = NeonPurple, contentDescription = "Selected")
                        } else {
                            Text("Select", color = TextTertiary, fontSize = 12.sp)
                        }
                    },
                    onClick = { viewModel.setActiveTheme(theme.id) }
                )
            }

            // Create custom theme option
            SettingsRow(
                label = "Create Custom Theme",
                subtitle = "Build your own color scheme",
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: Navigate to custom theme builder */ }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Section: Font Size
            SectionHeader(title = "Font Size")

            SettingsSlider(
                label = "Font Scale",
                value = fontScale,
                onValueChange = { viewModel.saveFontScale(it) },
                valueRange = 0.8f..1.5f,
                steps = 7,
                valueLabel = "${(fontScale * 100).toInt()}%"
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Section: Layout
            SectionHeader(title = "Layout")

            SettingsToggle(
                label = "Compact Mode",
                subtitle = "Show more content on screen",
                checked = true, // TODO: Add to settings
                onCheckedChange = { /* TODO */ }
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Section: Animation Speed
            SectionHeader(title = "Animation Speed")

            SettingsSlider(
                label = "Animation Scale",
                value = animationScale,
                onValueChange = { viewModel.saveAnimationScale(it) },
                valueRange = 0.0f..2.0f,
                steps = 10,
                valueLabel = "${(animationScale * 100).toInt()}%"
            )

            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Section: Accessibility Options
            SectionHeader(title = "Accessibility")

            SettingsRow(
                label = "Reduce Motion",
                subtitle = "Reduce animation effects",
                leadingIcon = { Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = NeonPurple) },
                trailing = {
                    Icon(
                        Icons.Default.CheckBoxOutlineBlank,
                        tint = TextTertiary,
                        contentDescription = "Disabled"
                    )
                }
            )

            SettingsRow(
                label = "High Contrast",
                subtitle = "Increase contrast for readability",
                leadingIcon = { Icon(Icons.Default.Contrast, contentDescription = null, tint = NeonPurple) },
                trailing = {
                    Icon(
                        Icons.Default.CheckBoxOutlineBlank,
                        tint = TextTertiary,
                        contentDescription = "Disabled"
                    )
                }
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