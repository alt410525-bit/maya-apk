@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.components.ScrollableColumn
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun YaadeinScreen(onBack: () -> Unit) {
    var showFront by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableStateOf(0) }

    val cards = remember {
        listOf(
            YaadeinCard(frontText = "What is your earliest memory?", backText = "Playing in the sandbox at age 4", masteryLevel = 5),
            YaadeinCard(frontText = "What are you grateful for today?", backText = "Good health and connection", masteryLevel = 4),
            YaadeinCard(frontText = "Describe your perfect day", backText = "Morning light, coffee, good conversation", masteryLevel = 3),
            YaadeinCard(frontText = "What's holding you back?", backText = "Fear of failure, waiting for the right moment", masteryLevel = 2),
            YaadeinCard(frontText = "What does happiness mean to you?", backText = "Freedom, creativity, meaningful work", masteryLevel = 1),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Yaadein",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, tint = TextSecondary, contentDescription = "Back")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CardDark
            )
        )

        // Content
        ScrollableColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current card
            if (cards.isNotEmpty()) {
                YaadeinCardUI(
                    card = cards[currentIndex],
                    showFront = showFront,
                    onFlip = { showFront = !showFront },
                    onMasteryChange = { level ->
                        // Update mastery level
                    }
                )
            }

            // Navigation buttons
            if (cards.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(text = "Previous", color = TextSecondary)
                    }
                    OutlinedButton(
                        onClick = { if (currentIndex < cards.size - 1) currentIndex++ },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(text = "Next", color = NeonCyan)
                    }
                }
            }
        }

        // FAB to add new card
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                tint = NeonPurple,
                contentDescription = "Add new yaadein"
            )
        }
    }
}

private data class YaadeinCard(
    val frontText: String,
    val backText: String,
    val masteryLevel: Int
)

@Composable
private fun YaadeinCardUI(
    card: YaadeinCard,
    showFront: Boolean,
    onFlip: () -> Unit,
    onMasteryChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            background = CardElevated
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // Card header with mastery stars
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.frontText,
                    color = if (showFront) TextPrimary else TextTertiary,
                    fontSize = if (showFront) 14.sp else 12.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Elide
                )

                // Mastery stars
                Stars(masteryLevel = card.masteryLevel)
            }

            // Front or back text
            Text(
                text = if (showFront) card.frontText else card.backText,
                color = if (showFront) TextPrimary else NeonCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Elide,
                maxLines = if (showFront) 3 else 1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action button
            if (showFront) {
                OutlinedButton(
                    onClick = onFlip,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Flip",
                        color = NeonCyan,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onFlip,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(text = "Flip", color = NeonCyan, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(text = "Study", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Stars(masteryLevel: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (level in 1..5) {
            Icon(
                if (level <= masteryLevel) Icons.Default.StarFill
                else Icons.Default.Star,
                tint = if (level <= masteryLevel) NeonPurple else TextTertiary,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}