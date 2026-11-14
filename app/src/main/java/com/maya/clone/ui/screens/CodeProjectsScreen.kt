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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.components.ScrollableColumn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CodeProjectsScreen(onBack: () -> Unit) {
    var createProjectVisible by remember { mutableStateOf(false) }

    val projects = remember {
        listOf(
            ProjectCard(id = 1, name = "Maya AI", language = "Kotlin", lastModified = "2 days ago", fileCount = 47),
            ProjectCard(id = 2, name = "Weather App", language = "Swift", lastModified = "1 week ago", fileCount = 12),
            ProjectCard(id = 3, name = "Chat Bot", language = "Python", lastModified = "3 days ago", fileCount = 89),
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
                    text = "Code Projects",
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Projects",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (createProjectVisible) {
                    OutlinedButton(onClick = { createProjectVisible = false }) {
                        Text(text = "Cancel", color = NeonCyan)
                    }
                } else {
                    OutlinedButton(
                        onClick = { createProjectVisible = true },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(text = "Create", color = NeonCyan)
                    }
                }
            }

            // Projects list
            if (projects.isEmpty()) {
                EmptyState(
                    title = "No Projects Yet",
                    description = "Create your first project",
                    buttonText = "Create Project",
                    onButtonClick = { createProjectVisible = true }
                )
            } else {
                items(projects, key = { it.id }) { project ->
                    ProjectCardUI(project = project)
                }
            }
        }
    }
}

private data class ProjectCard(
    val id: Int,
    val name: String,
    val language: String,
    val lastModified: String,
    val fileCount: Int
)

@Composable
private fun ProjectCardUI(project: ProjectCard) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
                // Language badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = project.language,
                        color = NeonPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Project name and details
                Column(
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = project.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${project.fileCount} files",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = project.lastModified,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Code, tint = NeonPurple, modifier = Modifier.size(32.dp), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onButtonClick) {
            Text(text = buttonText, color = NeonCyan)
        }
    }
}