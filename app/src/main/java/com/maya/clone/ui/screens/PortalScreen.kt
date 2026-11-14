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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun PortalScreen(onBack: () -> Unit) {
    var url by remember { mutableStateOf("") }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Portal",
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
            // URL input bar
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter URL", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = TextTertiary,
                    focusedContainerColor = CardDark,
 unfocusedContainerColor = CardDark
                ),
                placeholder = { Text("Enter URL", color = TextTertiary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Url)
            )

            // Nav buttons row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, tint = TextTertiary, contentDescription = "Back")
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Refresh, tint = TextTertiary, contentDescription = "Refresh")
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.ArrowForwardIos, tint = TextTertiary, contentDescription = "Forward")
                }
            }

            // Web content area placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(CardElevated),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Globe, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Portal content will appear here",
                    color = TextTertiary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}