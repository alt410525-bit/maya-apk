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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ScannerScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Scanner",
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

        // Content - camera view placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CardDark)
        ) {
            // Camera view placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NeonPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Camera, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(64.dp))
            }
        }

        // Bottom control bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardElevated)
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Flash toggle
            OutlinedButton(
                onClick = {},
                modifier = Modifier.size(44.dp).padding(start = 16.dp)
            ) {
                Icon(Icons.Default.FlashAuto, tint = TextTertiary, contentDescription = "Flash", modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Capture button (FAB circle)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(NeonPurple, CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, tint = Color.White, contentDescription = "Capture", modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Gallery button
            OutlinedButton(
                onClick = {},
                modifier = Modifier.size(44.dp).padding(end = 16.dp)
            ) {
                Icon(Icons.Default.ImageGallery, tint = TextTertiary, contentDescription = "Gallery", modifier = Modifier.size(20.dp))
            }
        }
    }
}