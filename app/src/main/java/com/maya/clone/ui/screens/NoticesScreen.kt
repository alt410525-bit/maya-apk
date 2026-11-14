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
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.components.ScrollableColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun NoticesScreen(onBack: () -> Unit) {
    var allRead by remember { mutableStateOf(false) }

    val notices = remember {
        listOf(
            NoticeItem(id = 1, icon = Icons.Default.NotificationsNone, title = "New Features", body = "Version 2.0 released with new AI capabilities", timestamp = "2 hours ago", unread = true),
            NoticeItem(id = 2, icon = Icons.Default.Info, title = "Maintenance", body = "System update scheduled for tonight at 2 AM", timestamp = "Yesterday", unread = false),
            NoticeItem(id = 3, icon = Icons.Default.Star, title = "Announcement", body = "New vibes pack available for download", timestamp = "3 days ago", unread = true),
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
                    text = "Notices",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Announcements",
                    color = NeonPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { allRead = true },
                    modifier = Modifier.size(60.dp)
                ) {
                    Text(
                        text = if (allRead) "Mark all read" else "All read",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }

            // Notices list
            items(notices, key = { it.id }) { notice ->
                NoticeCardUI(notice = notice, onMarkRead = {})
            }
        }
    }
}

private data class NoticeItem(
    val id: Int,
    val icon: Icons.Filled,
    val title: String,
    val body: String,
    val timestamp: String,
    val unread: Boolean
)

@Composable
private fun NoticeCardUI(notice: NoticeItem, onMarkRead: () -> Unit) {
    val isUnread = notice.unread

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isUnread) NeonPurple.copy(alpha = 0.1f) else CardElevated)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unread indicator
            if (isUnread) {
                Box(
                    modifier = Modifier.size(8.dp).background(NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                )
            }

            // Icon
            Box(
                modifier = Modifier.size(32.dp).background(CardDark, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    notice.icon,
                    tint = if (isUnread) NeonCyan else TextTertiary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Title and body
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = notice.title,
                    color = if (isUnread) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = notice.body,
                    color = if (isUnread) TextPrimary else TextTertiary,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            // Timestamp
            Text(
                text = notice.timestamp,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}