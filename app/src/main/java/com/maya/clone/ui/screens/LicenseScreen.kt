@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.maya.clone.ui.components.SettingsRow
import androidx.compose.foundation.border

@Composable
fun LicenseScreen(
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
            title = { Text("License", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Current tier
        SectionHeader(title = "Current Plan")

        val tiers = listOf(
            "Free" to "Free" to "basic_chat, basic_voice" to false,
            "Pro" to "Pro" to "advanced_voice, sub_agents, skills, backup, face_lock" to false,
            "Pro Max" to "Pro Max" to "all pro + connectors, auto_reply, awareness, triggers, priority" to true,
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            tiers.forEach { (name, subtitle, features, isCurrent) ->
                LicenseTierCard(
                    name = name,
                    subtitle = subtitle,
                    features = features,
                    isCurrent = isCurrent,
                    onSelect = { /* TODO: launch billing flow */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manage subscription
        SectionHeader(title = "Manage Subscription")

        SettingsRow(
            label = "Manage Subscription",
            subtitle = "View or change your plan in Play Store",
            leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open Play Store subscription */ }
        )
        SettingsRow(
            label = "Restore Purchases",
            subtitle = "Restore previous purchases",
            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: restore purchases */ }
        )
        SettingsRow(
            label = "Billing History",
            subtitle = "View past invoices and receipts",
            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open billing history */ }
        )

        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Legal
        SectionHeader(title = "Legal")

        SettingsRow(
            label = "Terms of Service",
            subtitle = "Read our terms and conditions",
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open TOS */ }
        )
        SettingsRow(
            label = "Privacy Policy",
            subtitle = "How we handle your data",
            leadingIcon = { Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = NeonPurple) },
            onClick = { onNavigate(Navigation.PRIVACY) }
        )
        SettingsRow(
            label = "Refund Policy",
            subtitle = "Learn about refunds",
            leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = NeonPurple) },
            onClick = { /* TODO: open refund policy */ }
        )
    }
}

@Composable
private fun LicenseTierCard(
    name: String,
    subtitle: String,
    features: String,
    isCurrent: Boolean,
    onSelect: () -> Unit
) {
    val bgColor = if (isCurrent) NeonPurple.copy(alpha = 0.15f) else CardDark
    val borderColor = if (isCurrent) NeonPurple else Color.Transparent
    val textColor = if (isCurrent) NeonPurple else TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, color = if (isCurrent) NeonPurple else TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (isCurrent) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CURRENT", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(features, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
        }

        if (isCurrent) {
            Icon(Icons.Default.CheckCircle, tint = NeonPurple, contentDescription = "Current plan", modifier = Modifier.size(24.dp))
        } else {
            Icon(Icons.Default.ChevronRight, tint = TextTertiary, contentDescription = "Select plan", modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, accentColor: Color = NeonPurple) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Divider(color = DividerColor, thickness = 1.dp)
    }
}