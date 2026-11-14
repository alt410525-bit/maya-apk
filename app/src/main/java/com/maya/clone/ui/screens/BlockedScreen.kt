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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.unit.sp

@Composable
fun BlockedScreen(onBack: () -> Unit) {
    var isAdding by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Blocked Contacts",
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
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Blocked Contacts",
                    color = NeonPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                FloatingActionButton(
                    onClick = { isAdding = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, tint = NeonPurple, contentDescription = "Add blocked contact")
                }
            }

            // Blocked contacts list
            if (blockedContacts.isEmpty()) {
                EmptyBlockedState(
                    onAddContact = { isAdding = true }
                )
            } else {
                var listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(blockedContacts, key = { it.id }) { contact ->
                        BlockedContactRow(contact = contact, onUnblock = {})
                    }
                }
            }
        }
    }
}

private val blockedContacts = remember {
    listOf(
        BlockedContact(id = 1, initials = "JA", name = "John Anderson"),
        BlockedContact(id = 2, initials = "SM", name = "Sarah Mitchell"),
    )
}

@Composable
private fun BlockedContactRow(contact: BlockedContact, onUnblock: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardElevated)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar initials
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.initials,
                    color = NeonPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    contentDescription = null
                )
            }

            // Contact name
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = contact.name,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap to unblock",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            // Unblock button
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onUnblock,
                modifier = Modifier.size(40.dp)
            ) {
                Text(
                    text = "Unblock",
                    color = Warning,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyBlockedState(onAddContact: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PersonOff, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No blocked contacts",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onAddContact) {
            Text(text = "Add Blocked Contact", color = NeonCyan)
        }
    }
}

private data class BlockedContact(
    val id: Int,
    val initials: String,
    val name: String
)