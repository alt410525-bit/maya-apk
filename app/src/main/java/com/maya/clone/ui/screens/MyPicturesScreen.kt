@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun MyPicturesScreen(onBack: () -> Unit) {
    var filter by remember { mutableStateOf("All") }
    val albums = remember {
        listOf(
            "Recent", "Favorites", "2024", "Screenshots"
        )
    }
    val photos = remember {
        listOf(
            PhotoItem(id = 1, thumbnail = null, date = "Today, 3:45 PM"),
            PhotoItem(id = 2, thumbnail = null, date = "Yesterday, 10:20 AM"),
            PhotoItem(id = 3, thumbnail = null, date = "2 days ago, 7:10 PM"),
            PhotoItem(id = 4, thumbnail = null, date = "3 days ago, 2:30 PM"),
            PhotoItem(id = 5, thumbnail = null, date = "4 days ago, 9:05 AM"),
            PhotoItem(id = 6, thumbnail = null, date = "5 days ago, 4:50 PM"),
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
                    text = "My Pictures",
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
            // Filter tabs
            TabRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                backgroundColor = CardElevated,
                indicatorBackground = NeonPurple
            ) {
                tabs = tabs { tab ->
                    Tab(
                        text = { Text(tab) },
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        if (filter == tab) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                }
            }
        }

        // Photo grid (2 columns)
        var listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            columns = { count: Int -> 2 },
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(photos, key = { it.id }) { photo ->
                PhotoGridItem(
                    thumbnail = null,
                    date = photo.date
                )
            }
        }

        // FAB with camera icon
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.Camera,
                tint = NeonPurple,
                contentDescription = "Take photo"
            )
        }
    }
}

private data class PhotoItem(
    val id: Int,
    var thumbnail: Any?, // BoxPainter or ImageBitmap
    val date: String
)

@Composable
private fun PhotoGridItem(
    thumbnail: Any?,
    date: String
) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .background(CardElevated)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Thumbnail placeholder
        if (thumbnail != null) {
            // Would show actual image
        } else {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Photo, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(40.dp))
            }
        }
    }

    Text(
        text = date,
        color = TextTertiary,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
    )
}