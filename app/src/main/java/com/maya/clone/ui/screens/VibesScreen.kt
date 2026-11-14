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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.components.ScrollableColumn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun VibesScreen(onBack: () -> Unit) {
    var selectedVibe by remember { mutableStateOf("😊 Content") }

    val vibes = remember {
        listOf(
            VibeItem(id = 1, emoji = "😊", name = "Content", description = "Feeling productive and happy", color = NeonPurple),
            VibeItem(id = 2, emoji = "😌", name = "Peaceful", description = "Calm and relaxed state", color = NeonCyan),
            VibeItem(id = 3, emoji = "😄", name = "Energetic", description = "Full of life and motivation", color = NeonPink),
            VibeItem(id = 4, emoji = "😐", name = "Neutral", description = "Neutral mood, just okay", color = TextTertiary),
            VibeItem(id = 5, emoji = "😊", name = "Content", description = "Feeling productive and happy", color = AwarenessColor),
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
                    text = "Vibes",
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
            ),
        ) {
            Unit
        }

        // Content
        ScrollableColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vibe grid
            GridCells(
                columns = 3,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vibes, key = { it.id }) { vibe ->
                    VibeCardUI(vibe = vibe, isSelected = selectedVibe == vibe.name)
                        .onClick = { selectedVibe = vibe.name }
                }
            }
        }

        // Set vibe button
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                text = "Set as Active Vibe",
                color = NeonCyan,
                fontSize = 14.sp
            )
        }
    }
}

private data class VibeItem(
    val id: Int,
    val emoji: String,
    val name: String,
    val description: String,
    val color: Color
)

@Composable
private fun VibeCardUI(
    vibe: VibeItem,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) vibe.color.copy(alpha = 0.2f) else CardElevated)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Center
        ) {
            // Emoji
            Text(
                text = vibe.emoji,
                color = vibe.color,
                fontSize = 32.dp,
            )

            // Name
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = vibe.name,
                color = vibe.color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            // Description
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = vibe.description,
                color = TextTertiary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GridCells(
    columns: Int = 2,
    verticalArrangement: Arrangement.Default = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Default = Arrangement.spacedBy(8.dp)
) {
    var columnCount by remember { mutableStateOf(columns) }
    var itemCount by remember { mutableStateOf(0) }

    // This is a simplified grid - in production would measure content
    Column(modifier = Modifier.fillMaxWidth()) {
        // Grid would be composed here
        // For now just show placeholder
        items(5) { i ->
            // placeholder
        }
    }
}