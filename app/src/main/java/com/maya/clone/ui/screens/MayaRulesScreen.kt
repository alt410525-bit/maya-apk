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
import androidx.compose.ui.unit.sp

@Composable
fun MayaRulesScreen(onBack: () -> Unit) {
    var isCreating by remember { mutableStateOf(false) }

    val rules = remember {
        listOf(
            RuleCard(
                id = 1,
                name = "Morning Greeting",
                trigger = "Time: 8:00 AM",
                action = "Say hello",
                enabled = true
            ),
            RuleCard(
                id = 2,
                name = "Low Battery",
                trigger = "Battery < 20%",
                action = "Notify me",
                enabled = true
            ),
            RuleCard(
                id = 3,
                name = "Location Arrival",
                trigger = "Home reached",
                action = "Auto-check in",
                enabled = false
            ),
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
                    text = "Automation Rules",
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
            // Rules list
            if (rules.isEmpty()) {
                EmptyState(
                    title = "No Rules Yet",
                    description = "Create your first automation rule",
                    buttonText = "Create Rule",
                    onButtonClick = { isCreating = true }
                )
            } else {
                items(rules, key = { it.id }) { rule ->
                    RuleCardUI(rule = rule)
                        .padding(vertical = 4.dp)
                }
            }

            // Create rule FAB
            FloatingActionButton(
                onClick = { isCreating = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    tint = NeonPurple,
                    contentDescription = "Create new rule"
                )
            }
        }
    }
}

private data class RuleCard(
    val id: Int,
    val name: String,
    val trigger: String,
    val action: String,
    val enabled: Boolean
)

@Composable
private fun RuleCardUI(rule: RuleCard) {
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
                // Rule name
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = rule.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = rule.trigger,
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = rule.action,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Enabled toggle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (rule.enabled) NeonPurple.copy(alpha = 0.15f) else CardDark,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (rule.enabled) Icons.Default.CheckboxChecked
                        else Icons.Default.CheckboxIndeterminate,
                        tint = if (rule.enabled) NeonPurple else TextTertiary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}