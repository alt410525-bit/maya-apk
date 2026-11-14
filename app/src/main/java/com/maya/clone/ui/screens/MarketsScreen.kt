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
fun MarketsScreen(onBack: () -> Unit) {
    var watchlistVisible by remember { mutableStateOf(true) }
    var savedVisible by remember { mutableStateOf(false) }
    var alertsVisible by remember { mutableStateOf(false) }

    val watchlist = remember {
        listOf(
            MarketStock(symbol = "AAPL", price = "$185.42", change = "+2.3%"),
            MarketStock(symbol = "GOOGL", price = "$2,850.10", change = "-0.7%"),
            MarketStock(symbol = "MSFT", price = "$425.80", change = "+1.1%"),
        )
    }

    val saved = remember {
        listOf(
            MarketStock(symbol = "TSLA", price = "$265.30", change = "+3.5%"),
        )
    }

    val alerts = remember {
        listOf(
            MarketAlert(symbol = "NVDA", price = "$945.20", change = "-5.2%", alertType = "Below target"),
            MarketAlert(symbol = "AMZN", price = "$189.50", change = "+1.8%", alertType = "Above target"),
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
                    text = "Markets",
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
            // Watchlist/saved/alerts tabs
            TabRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                backgroundColor = CardElevated,
                indicatorBackground = NeonPurple
            ) {
                tabs { tab ->
                    Tab(text = { Text("Watchlist") }) {
                        if (watchlistVisible) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                    Tab(text = { Text("Saved") }) {
                        if (savedVisible) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                    Tab(text = { Text("Alerts") }) {
                        if (alertsVisible) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                        }
                    }
                }
            }

            // Content based on selected tab
            when (selectedTab = "Watchlist") {
                watchlistVisible -> {
                    WatchlistSection(stocks = watchlist)
                }
                !watchlistVisible -> {
                    SavedSection(stocks = saved)
                }
            }
            AlertsSection(alerts = alerts)
        }
    }
}

private data class MarketStock(
    val symbol: String,
    val price: String,
    val change: String
)

private data class MarketAlert(
    val symbol: String,
    val price: String,
    val change: String,
    val alertType: String
)

@Composable
private fun WatchlistSection(stocks: List<MarketStock>) {
    Text(
        text = "Watchlist",
        color = NeonPurple,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    var listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(stocks, key = { it.symbol }) { stock ->
            MarketStockCard(symbol = stock.symbol, price = stock.price, change = stock.change)
        }
    }
}

@Composable
private fun SavedSection(stocks: List<MarketStock>) {
    Text(
        text = "Saved",
        color = NeonPurple,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    var listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(stocks, key = { it.symbol }) { stock ->
            MarketStockCard(symbol = stock.symbol, price = stock.price, change = stock.change)
        }
    }
}

@Composable
private fun AlertsSection(alerts: List<MarketAlert>) {
    Text(
        text = "Alerts",
        color = Warning,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    var listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(alerts, key = { it.symbol }) { alert ->
            MarketAlertCard(symbol = alert.symbol, price = alert.price, change = alert.change, alertType = alert.alertType)
        }
    }
}

@Composable
private fun MarketStockCard(symbol: String, price: String, change: String) {
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
            Text(
                text = symbol,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = price,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Text(
                text = change,
                color = if (change.startsWith("+")) Success else Warning,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MarketAlertCard(symbol: String, price: String, change: String, alertType: String) {
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
            // Alert badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Warning.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = alertType,
                    color = Warning,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Alert info
            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = symbol,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = price,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Text(
                text = change,
                color = if (change.startsWith("+")) Success else Warning,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}