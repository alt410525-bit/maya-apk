@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.keyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.SettingsVoiceEngineViewModel
import com.maya.clone.ui.components.SettingsRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DialogProperties
import androidx.compose.material3.TextButton

@Composable
fun SettingsVoiceEngineScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsVoiceEngineViewModel = hiltViewModel()
) {
    val selectedSTTEngine by viewModel.selectedSTTEngine.collectAsState()
    val selectedTTSEngine by viewModel.selectedTTSEngine.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val availableLanguages by viewModel.availableLanguages.collectAsState()
    val isDownloadingLanguage by viewModel.isDownloadingLanguage.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val isTestingTTS by viewModel.isTestingTTS.collectAsState()
    val ttsStatus by viewModel.ttsStatus.collectAsState()
    val sttStatus by viewModel.sttStatus.collectAsState()

    // Dialog states
    var showSTTEngineDialog by remember { mutableStateOf(false) }
    var showTTSEngineDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar with back arrow
        TopAppBar(
            title = { Text("Voice Engine", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: STT Engine Selection
        SectionHeader(title = "Speech Recognition (STT)")

        SettingsRow(
            label = "STT Engine",
            subtitle = "Select speech-to-text engine",
            leadingIcon = { Icon(Mic, tint = NeonCyan, contentDescription = "STT engine") },
            trailing = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedSTTEngine.label, color = TextSecondary, fontSize = 14.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextTertiary)
                }
            },
            onClick = {
                showSTTEngineDialog = true
            }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Section: TTS Engine Selection
        SectionHeader(title = "Text-to-Speech (TTS)")

        SettingsRow(
            label = "TTS Engine",
            subtitle = "Select text-to-speech engine",
            leadingIcon = { Icon(PlayArrow, tint = NeonPurple, contentDescription = "TTS engine") },
            trailing = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedTTSEngine.label, color = TextSecondary, fontSize = 14.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextTertiary)
                }
            },
            onClick = {
                showTTSEngineDialog = true
            }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Language Selection
        SettingsRow(
            label = "Language",
            subtitle = "Voice recognition and synthesis language",
            leadingIcon = { Icon(Language, tint = NeonCyan, contentDescription = "Language") },
            trailing = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedLanguage, color = TextSecondary, fontSize = 14.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextTertiary)
                }
            },
            onClick = {
                showLanguageDialog = true
            }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Section: Language Download
        SectionHeader(title = "Offline Language Packs")

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableLanguages) { lang ->
                val isSelected = lang == selectedLanguage
                SettingsRow(
                    label = lang,
                    subtitle = if (isSelected) "Selected" else "Tap to download",
                    leadingIcon = { Icon(Language, tint = if (isSelected) NeonPurple else NeonCyan, contentDescription = "Language") },
                    trailing = {
                        if (isDownloadingLanguage && isSelected) {
                            Column(
                                modifier = Modifier.width(80.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                LinearProgressIndicator(
                                    progress = downloadProgress / 100f,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = NeonPurple,
                                    trackColor = NeonPurple.copy(alpha = 0.2f)
                                )
                                Text("${downloadProgress}%", color = TextSecondary, fontSize = 10.sp)
                            }
                        } else if (!isSelected) {
                            IconButton(onClick = { viewModel.downloadLanguage(lang) }) {
                                Icon(Download, tint = NeonPurple, contentDescription = "Download $lang")
                            }
                        } else {
                            Icon(Icons.Default.CheckCircle, tint = NeonPurple, contentDescription = "Selected")
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Section: Test Playback
        SectionHeader(title = "Test Voice Engine")

        SettingsRow(
            label = "Test TTS Playback",
            subtitle = "Play test audio with current settings",
            leadingIcon = { Icon(PlayArrow, tint = NeonPurple, contentDescription = "Test TTS") },
            trailing = {
                if (isTestingTTS) {
                    CircularProgressIndicator(color = NeonPurple, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.PlayArrow, tint = NeonPurple, contentDescription = "Play test")
                }
            },
            onClick = { viewModel.testTTSPlayback() }
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))

        SettingsRow(
            label = "Test STT Recognition",
            subtitle = "Test speech recognition",
            leadingIcon = { Icon(Mic, tint = NeonCyan, contentDescription = "Test STT") },
            trailing = {
                Icon(Icons.Default.Mic, tint = NeonCyan, contentDescription = "Start listening")
            },
            onClick = { viewModel.testSTTRecognition() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Status display
        if (ttsStatus.isNotBlank() || sttStatus.isNotBlank()) {
            SectionHeader(title = "Status")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (ttsStatus.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(PlayArrow, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(ttsStatus, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                    if (sttStatus.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Mic, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(sttStatus, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Dialog overlays
        if (showSTTEngineDialog) {
            STTEngineSelectionDialog(
                engines = SettingsVoiceEngineViewModel.STTEngine.values().toList(),
                selectedEngine = selectedSTTEngine,
                onEngineSelected = { engine ->
                    viewModel.onSTTEngineSelected(engine)
                    showSTTEngineDialog = false
                },
                onDismiss = { showSTTEngineDialog = false }
            )
        }

        if (showTTSEngineDialog) {
            TTSEngineSelectionDialog(
                engines = SettingsVoiceEngineViewModel.TTSEngine.values().toList(),
                selectedEngine = selectedTTSEngine,
                onEngineSelected = { engine ->
                    viewModel.onTTSEngineSelected(engine)
                    showTTSEngineDialog = false
                },
                onDismiss = { showTTSEngineDialog = false }
            )
        }

        if (showLanguageDialog) {
            LanguageSelectionDialog(
                languages = availableLanguages,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { lang ->
                    viewModel.onLanguageSelected(lang)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

// Dialog functions
@Composable
fun STTEngineSelectionDialog(
    engines: List<SettingsVoiceEngineViewModel.STTEngine>,
    selectedEngine: SettingsVoiceEngineViewModel.STTEngine,
    onEngineSelected: (SettingsVoiceEngineViewModel.STTEngine) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select STT Engine", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                engines.forEach { engine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable { onEngineSelected(engine) }
                            .background(CardDark)
                    ) {
                        Text(engine.label, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f).padding(16.dp))
                        if (engine == selectedEngine) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = NeonPurple) } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun TTSEngineSelectionDialog(
    engines: List<SettingsVoiceEngineViewModel.TTSEngine>,
    selectedEngine: SettingsVoiceEngineViewModel.TTSEngine,
    onEngineSelected: (SettingsVoiceEngineViewModel.TTSEngine) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select TTS Engine", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                engines.forEach { engine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable { onEngineSelected(engine) }
                            .background(CardDark)
                    ) {
                        Text(engine.label, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f).padding(16.dp))
                        if (engine == selectedEngine) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = NeonPurple) } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun LanguageSelectionDialog(
    languages: List<String>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                languages.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable { onLanguageSelected(lang) }
                            .background(CardDark)
                    ) {
                        Text(lang, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f).padding(16.dp))
                        if (lang == selectedLanguage) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = NeonPurple) } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
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