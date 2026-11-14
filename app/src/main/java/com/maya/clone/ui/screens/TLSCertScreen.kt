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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TLSCertScreen(onBack: () -> Unit) {
    var certStatus by remember { mutableStateOf("Valid") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "TLS Certificate",
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
            // Certificate status
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = 1.dp,
                colors = CardDefaults.cardColors(
                    background = CardElevated
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (certStatus == "Valid") NeonPurple.copy(alpha = 0.15f)
                    else if (certStatus == "Invalid") Warning.copy(alpha = 0.15f)
                    else Error.copy(alpha = 0.15f),
                RoundedCornerShape(14.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = certStatus,
                color = if (certStatus == "Valid") NeonPurple
                else if (certStatus == "Invalid") Warning
                else Error,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

                        // Trigger refresh button
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedButton(
                            onClick = { certStatus = "Valid" },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text(text = "Refresh", color = NeonCyan)
                        }
                    }

                    // Certificate details
                    Text(
                        text = "Issuer: Maya CA",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Valid from: January 15, 2026",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Valid to: January 15, 2027",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Fingerprint: A1B2C3D4E5F60718293A4B5C6D7E8F901234",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                }
            }

            // Refresh button at bottom
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(text = "Refresh Certificate", color = NeonCyan)
            }
        }
    }
}