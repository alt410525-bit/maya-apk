@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*
import com.maya.clone.ui.viewmodel.SettingsConnectorsViewModel
import com.maya.clone.ui.components.SettingsRow

@Composable
fun SettingsConnectorsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsConnectorsViewModel = hiltViewModel()
) {
    val userId = "default_user"
    viewModel.init(userId)

    val connectors by viewModel.connectors.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Connectors", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        error?.let { errorMsg ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) { Text("Error: $errorMsg", color = Error, fontSize = 12.sp) }
        }

        SectionHeader(title = "Integrations")

        if (isLoading) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = NeonPurple)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading connectors...", color = TextSecondary, fontSize = 13.sp)
            }
        } else if (connectors.isEmpty()) {
            SettingsRow(
                label = "No connectors configured",
                subtitle = "Add integrations to extend Maya's capabilities",
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = NeonPurple) },
                onClick = { /* TODO: show add connector dialog */ }
            )
        } else {
            connectors.forEach { connector ->
                ConnectorRow(
                    connector = connector,
                    onDisconnect = { viewModel.disconnectConnector(connector.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add new connector
        SettingsRow(
            label = "Add Connector",
            subtitle = "Connect Google, GitHub, Notion, etc.",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: show add connector dialog */ }
        )
    }
}

@Composable
private fun ConnectorRow(
    connector: com.maya.clone.data.entity.ConnectorEntity,
    onDisconnect: () -> Unit
) {
    val isConnected = connector.status == "connected"
    val statusColor = if (isConnected) Success else Warning

    SettingsRow(
        label = connector.type.capitalize(),
        subtitle = connector.configJson.substring(0, minOf(50, connector.configJson.length)).ifEmpty { "Configured" } + "...",
        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = if (isConnected) NeonCyan else NeonPurple) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    connector.status.capitalize(),
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isConnected) {
                    IconButton(onClick = onDisconnect) {
                        Icon(Icons.Default.Delete, tint = Error, contentDescription = "Disconnect ${connector.type}")
                    }
                }
            }
        }
    )
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}