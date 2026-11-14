@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush

@Composable
fun MarketingScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Maya AI Assistant", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hero section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(NeonPurple, NeonCyan),
                        start = Alignment.TopStart,
                        end = Alignment.BottomEnd
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Maya AI Assistant",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.TextAlign.Center
                )
                Text(
                    "Your intelligent companion — chat, voice, automation, and more",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { /* TODO: open Play Store */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = NeonPurple
                        )
                    ) {
                        Text("Download on Play Store", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = { /* TODO: share app */ },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            borderColor = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Share with Friends", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Features
        SectionHeader(title = "Why Choose Maya?")

        val features = listOf(
            "🤖 Smart AI Chat" to "GPT-4 powered conversations with context awareness",
            "🎤 Voice Interface" to "Natural speech-to-text and text-to-speech",
            "🔒 Face Lock Security" to "Biometric app locking with ML Kit",
            "☁️ Cloud Sync" to "Encrypted backup and cross-device sync",
            "🤖 Sub-Agents" to "Specialized AI personas for coding, writing, analysis",
            "🔧 Automation" to "Custom triggers, auto-reply, and smart rules",
            "🛡️ Privacy First" to "Local processing, encrypted backups, your data",
            "🎨 Customizable" to "Themes, fonts, animations, and accessibility",
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            features.forEach { (title, description) ->
                FeatureHighlight(title = title, description = description)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Social proof
        SectionHeader(title = "Trusted by Users")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RatingCard(stars = 5, text = "4.8 ★", count = "10K+ reviews")
            RatingCard(stars = 5, text = "4.7 ★", count = "50K+ downloads")
            RatingCard(stars = 5, text = "4.9 ★", count = "Editor's Choice")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CTA
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Ready to experience the future of AI assistance?",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.TextAlign.Center
            )
            Text(
                "Join thousands of users who trust Maya daily",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.TextAlign.Center
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* TODO: open Play Store */ },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Get Started Free", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                OutlinedButton(
                    onClick = { /* TODO: share */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonPurple,
                        borderColor = NeonPurple
                    )
                ) {
                    Text("Share Maya", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("© 2026 Maya AI Assistant", color = TextTertiary, fontSize = 12.sp)
            Text("Privacy Policy • Terms of Service • Contact Us", color = TextTertiary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun FeatureHighlight(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(NeonPurple.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(description, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun RatingCard(stars: Int, text: String, count: String) {
    val starRow = Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(stars) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        starRow
        Text(text, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(count, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Divider(color = accentColor.copy(alpha = 0.3f), height = 2.dp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp))
    }
}