@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    conversationId: String,
    userId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    viewModel.init(conversationId, userId)
    
    val messages by viewModel.messages.collectAsStateWithLifecycle(emptyList())
    val isStreaming by viewModel.isStreaming.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)
    
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // ── Top bar ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(CardDark, CircleShape)) {
                Icon(Icons.Default.ArrowBack, tint = TextSecondary, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.size(40.dp).background(NeonPurple.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Maya", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("AI Assistant · Online", color = NeonCyan, fontSize = 11.sp)
            }
            IconButton(onClick = { /* agent picker */ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Person, tint = TextSecondary, contentDescription = "Agents")
            }
            IconButton(onClick = { /* more */ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.MoreVert, tint = TextSecondary, contentDescription = "More")
            }
        }

        HorizontalDivider(color = DividerColor)

        // ── Message list ─────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    EmptyChatState()
                }
            }
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg)
            }
        }

        // Error display
        if (error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Error: $error",
                    color = Error,
                    fontSize = 12.sp
                )
            }
        }

        // ── Quick suggestions (when empty) ─────────────────────
        if (messages.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Today's news", "Set alarm 7AM", "Help me code").forEach { suggestion ->
                    SuggestionChip(suggestion = suggestion, onClick = {
                        viewModel.sendMessage(suggestion)
                    })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Input bar ─────────────────────────────────────────
        HorizontalDivider(color = DividerColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment
            IconButton(onClick = { /* attach */ }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.AttachFile, tint = TextTertiary, contentDescription = "Attach")
            }

            // Text input
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardDark),
                placeholder = { Text("Ask Maya anything…", color = TextTertiary, fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Mic / Send / Voice
            if (inputText.isBlank() && !isListening) {
                // VoiceButton placeholder - integrates with voice input
                IconButton(
                    onClick = { isListening = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonCyan, CircleShape)
                ) {
                    Icon(
                        Icons.Default.MicNone,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                // TODO: Replace with VoiceButton component when available
                // VoiceButton(
                //     state = VoiceButtonState.Idle,
                //     onClick = { isListening = true },
                //     modifier = Modifier.size(48.dp)
                // )
            } else if (isListening) {
                IconButton(
                    onClick = { isListening = false },
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonPurple, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonCyan, CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Listening indicator
        if (isListening) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(Error, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Listening…", color = Error, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Tap mic to stop", color = TextTertiary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun EmptyChatState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(72.dp).background(NeonPurple.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, tint = NeonPurple, modifier = Modifier.size(36.dp), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Maya", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Your AI assistant — tap mic or type to start", color = TextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ChatBubble(msg: com.maya.clone.data.entity.MessageEntity) {
    val isUser = msg.role == "user"
    val isStreaming = msg.status == "streaming"
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp, topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(if (isUser) NeonCyan else CardDark)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    msg.content,
                    color = if (isUser) Color.Black else TextPrimary,
                    fontSize = 14.sp
                )
                if (isStreaming) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = if (isUser) Color.Black.copy(alpha = 0.7f) else NeonCyan,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            msg.createdAt.toString(), // TODO: Format timestamp
            color = TextTertiary,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun SuggestionChip(suggestion: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(suggestion, color = TextSecondary, fontSize = 12.sp)
    }
}
