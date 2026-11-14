@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.clone.Navigation
import com.maya.clone.data.entity.AgentEntity
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.HomeViewModel

data class FeatureCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Initialize with a default userId - in a real app this would come from auth
    val userId = "default_user"
    viewModel.init(userId)
    
    val agents by viewModel.agents.collectAsStateWithLifecycle(emptyList())
    val recentConversations by viewModel.recentConversations.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── Top bar ──────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Hey there 👋",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    "Maya",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = { onNavigate(Navigation.SETTINGS) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(CardDark, CircleShape)
                ) {
                    Icon(Icons.Default.Settings, tint = TextSecondary, contentDescription = "Settings")
                }
                IconButton(
                    onClick = { onNavigate("chat") },
                    modifier = Modifier
                        .size(44.dp)
                        .background(NeonPurple, CircleShape)
                ) {
                    Icon(Icons.Default.Chat, tint = TextPrimary, contentDescription = "Chat")
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Orb / Maya status ────────────────────────────────
        val orbColors = listOf(NeonPurple, NeonCyan, NeonPink)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(orbColors)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Maya is online",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tap to talk • Voice & text",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Dynamic Agents Grid ──────────────────────────────
        Text(
            "Your Agents",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonCyan)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading agents...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (agents.isEmpty()) {
            // Empty state - show create agent option
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark, RoundedCornerShape(16.dp))
                    .padding(20.dp)
                    .clickable { onNavigate("agent_picker") },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(NeonPurple.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No agents yet",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Tap to create your first AI agent",
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                agents.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { agent ->
                            AgentCardItem(
                                agent = agent,
                                onClick = { onNavigate("chat") }, // Navigate to chat with this agent
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // fill empty slots to maintain grid
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Feature grid (static features) ────────────────────
        val features = listOf(
            FeatureCard("Awareness", "Screen context & memory", Icons.Default.Visibility, AwarenessColor, "awareness_log"),
            FeatureCard("Files & Docs", "Documents & photos", Icons.Default.Folder, FilesColor, "documents"),
            FeatureCard("Scanner", "Read screen & content", Icons.Default.QrCodeScanner, SettingsColor, "scanner"),
            FeatureCard("Market", "Connect & explore", Icons.Default.Store, NeonPink, "markets"),
            FeatureCard("Maya Rules", "Automation & triggers", Icons.Default.AutoAwesome, VoiceColor, "maya_rules"),
            FeatureCard("Photos", "Gallery & yaadein", Icons.Default.PhotoLibrary, FilesColor, "my_pictures"),
            FeatureCard("Whiteboard", "Canvas & ideas", Icons.Default.Draw, TextSecondary, "whiteboard"),
            FeatureCard("Settings", "Preferences & config", Icons.Default.Settings, SettingsColor, Navigation.SETTINGS),
            FeatureCard("Agents", "Switch AI persona", Icons.Default.Person, ChatColor, "agent_picker"),
            FeatureCard("Vibes", "Mood & reactions", Icons.Default.Mood, NeonPink, "vibes"),
            FeatureCard("Code", "Projects & tools", Icons.Default.Code, ChatColor, "code_projects"),
        )

        Text(
            "Features",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 3-column grid, 2 rows visible initially
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            features.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { feature ->
                        FeatureCardItem(
                            feature = feature,
                            onClick = { onNavigate(feature.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // fill empty slots to maintain grid
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Error display
        error?.let { errorMsg ->
            Text(
                "Error: $errorMsg",
                color = Error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // ── Bottom action bar ────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomAction(icon = Icons.Default.Home, label = "Home", active = true, onClick = {})
            BottomAction(icon = Icons.Default.Chat, label = "Chat", active = false, onClick = { onNavigate("chat") })
            BottomAction(icon = Icons.Default.Notifications, label = "Alerts", active = false, onClick = {})
            BottomAction(icon = Icons.Default.Person, label = "Agents", active = false, onClick = { onNavigate("agent_picker") })
            BottomAction(icon = Icons.Default.Settings, label = "Settings", active = false, onClick = { onNavigate(Navigation.SETTINGS) })
        }
    }
}

@Composable
private fun FeatureCardItem(feature: FeatureCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(feature.color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(feature.icon, contentDescription = null, tint = feature.color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            feature.title,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        Text(
            feature.subtitle,
            color = TextTertiary,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun AgentCardItem(agent: AgentEntity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(NeonPurple.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            agent.name,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        Text(
            agent.model,
            color = TextTertiary,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun BottomAction(icon: ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Icon(icon, contentDescription = label, tint = if (active) NeonCyan else TextTertiary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = if (active) NeonCyan else TextTertiary, fontSize = 10.sp)
    }
}
