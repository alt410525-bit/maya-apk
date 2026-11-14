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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun PhotosOfScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("People") }

    // People tab data
    val people = remember {
        listOf(
            PhotoPerson(id = 1, name = "Alice Johnson", photoCount = 42),
            PhotoPerson(id = 2, name = "Bob Smith", photoCount = 31),
            PhotoPerson(id = 3, name = "Charlie Brown", photoCount = 18),
        )
    }

    // Dates tab data
    val dates = remember {
        listOf(
            PhotoDate(id = 1, date = "Today", count = 15),
            PhotoDate(id = 2, date = "Yesterday", count = 8),
            PhotoDate(id = 3, date = "Last Week", count = 5),
        )
    }

    // Places tab data
    val places = remember {
        listOf(
            PhotoPlace(id = 1, name = "Home", count = 67),
            PhotoPlace(id = 2, name = "Work", count = 43),
            PhotoPlace(id = 3, name = "Cafe", count = 21),
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
                    text = "Photos Of",
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
            // Tab layout
            TabRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                backgroundColor = CardElevated,
                indicatorBackground = NeonPurple
            ) {
                tabs { tab ->
                    Tab(text = { Text("People") }) {
                        if (selectedTab == "People") {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                    Tab(text = { Text("Dates") }) {
                        if (selectedTab == "Dates") {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                    Tab(text = { Text("Places") }) {
                        if (selectedTab == "Places") {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                "People" -> {
                    PeopleGrid(people = people)
                }
                "Dates" -> {
                    DatesGrid(dates = dates)
                }
                "Places" -> {
                    PlacesGrid(places = places)
                }
            }
        }

        // Add detection button
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                text = "Add Person Detection",
                color = NeonCyan,
                fontSize = 14.sp
            )
        }
    }
}

private data class PhotoPerson(
    val id: Int,
    val name: String,
    val photoCount: Int
)

private data class PhotoDate(
    val id: Int,
    val date: String,
    val count: Int
)

private data class PhotoPlace(
    val id: Int,
    val name: String,
    val count: Int
)

@Composable
private fun PeopleGrid(people: List<PhotoPerson>) {
    var listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        columns = { count: Int -> 3 },
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(people, key = { it.id }) { person ->
            PersonGridItem(name = person.name, count = person.photoCount)
        }
    }
}

@Composable
private fun DatesGrid(dates: List<PhotoDate>) {
    var listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        columns = { count: Int -> 3 },
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(dates, key = { it.id }) { date ->
            DateGridItem(date = date.date, count = date.count)
        }
    }
}

@Composable
private fun PlacesGrid(places: List<PhotoPlace>) {
    var listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        columns = { count: Int -> 3 },
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(places, key = { it.id }) { place ->
            PlaceGridItem(name = place.name, count = place.photoCount)
        }
    }
}

@Composable
private fun PersonGridItem(name: String, count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardElevated)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Center
        ) {
            // Placeholder photo
            Box(
                modifier = Modifier.size(60.dp).background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(24.dp))
            }

            Text(
                text = name,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = "$count photos",
                color = TextTertiary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun DateGridItem(date: String, count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardElevated)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Center
        ) {
            // Placeholder photo
            Box(
                modifier = Modifier.size(60.dp).background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarMonth, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(24.dp))
            }

            Text(
                text = date,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = "$count photos",
                color = TextTertiary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun PlaceGridItem(name: String, count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardElevated)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Center
        ) {
            // Placeholder photo
            Box(
                modifier = Modifier.size(60.dp).background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(24.dp))
            }

            Text(
                text = name,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = "$count photos",
                color = TextTertiary,
                fontSize = 9.sp
            )
        }
    }
}