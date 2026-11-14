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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun DocumentsScreen(onBack: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf<String?>(nil) }

    val allDocuments = remember {
        listOf(
            DocumentFile(id = 1, name = "Project Proposal.pdf", size = "2.3 MB", date = "Yesterday", folder = false),
            DocumentFile(id = 2, name = "Presentation.pptx", size = "4.1 MB", date = "2 days ago", folder = false),
            DocumentFile(id = 3, name = "Documents", folder = true),
            DocumentFile(id = 4, name = "Photos", folder = true),
            DocumentFile(id = 5, name = "Invoice Q4.pdf", size = "1.8 MB", date = "3 days ago", folder = false),
        )
    }

    val recentDocuments = allDocuments.filter { !it.folder }
    val folderDocuments = allDocuments.filter { it.folder }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Documents",
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
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search documents", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = TextTertiary,
                    focusedContainerColor = CardDark,
 unfocusedContainerColor = CardDark
                ),
                placeholder = { Text("Search documents", color = TextTertiary) },
                singleLine = true
            )

            // Folders section
            if (folderDocuments.isNotEmpty()) {
                Text(
                    text = "Folders",
                    color = NeonPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                items(folderDocuments, key = { it.id }) { folder ->
                    DocumentFolderItem(folderName = folder.name)
                        .padding(vertical = 4.dp)
                }
            }

            // Recent section
            if (recentDocuments.isNotEmpty()) {
                Text(
                    text = "Recent",
                    color = NeonPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )

                items(recentDocuments, key = { it.id }) { doc ->
                    DocumentItem(
                        fileName = doc.name,
                        fileSize = doc.size,
                        fileDate = doc.date,
                        onClick = {}
                    )
                        .padding(vertical = 4.dp)
                }
            }
        }
    }
}

private data class DocumentFile(
    val id: Int,
    val name: String,
    val size: String,
    val date: String,
    val folder: Boolean
)

@Composable
private fun DocumentItem(
    fileName: String,
    fileSize: String,
    fileDate: String,
    onClick: () -> Unit
) {
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
            // File icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(16.dp))
            }

            // File info
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = fileName,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = fileSize,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
                Text(
                    text = fileDate,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            // Action
            IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.MoreVert, tint = TextTertiary, contentDescription = "More options")
            }
        }
    }
}

@Composable
private fun DocumentFolderItem(folderName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Folder, tint = NeonPurple, contentDescription = null, modifier = Modifier.size(14.dp))
        }

        Text(
            text = folderName,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(16.dp))
    }
}