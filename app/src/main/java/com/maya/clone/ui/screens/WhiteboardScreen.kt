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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.clone.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight

@Composable
fun WhiteboardScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Whiteboard",
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
        ) {
            Unit
        }

        // Content - drawing area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CardDark)
        ) {
            // Drawing canvas placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NeonPurple.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pen, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(48.dp))
            }
        }

        // Bottom toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardElevated)
                .height(56.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Pen colours
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colourCircle(NeonPurple)
                colourCircle(NeonCyan)
                colourCircle(NeonPink)
                colourCircle(Color(0xFF06D6A0))
                colourCircle(Color(0xFFFF6B6B))
                colourCircle(Color(0xFFFFA07A))
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Eraser
            OutlinedButton(
                onClick = {},
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Eraser, tint = TextTertiary, contentDescription = "Eraser", modifier = Modifier.size(18.dp))
            }

            // Undo
            OutlinedButton(
                onClick = {},
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Undo, tint = TextTertiary, contentDescription = "Undo", modifier = Modifier.size(18.dp))
            }

            // Clear
            OutlinedButton(
                onClick = {},
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Delete, tint = Warning, contentDescription = "Clear", modifier = Modifier.size(18.dp))
            }

            // Save
            OutlinedButton(
                onClick = {},
                modifier = Modifier.size(36.dp).padding(start = 24.dp)
            ) {
                Icon(Icons.Default.Save, tint = NeonPurple, contentDescription = "Save", modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun colourCircle(colour: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(colour, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Small white dot to indicate selected
        // TODO: Add selection logic
    }
}