package com.example.tiffin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tiffin.analytics.Analytics
import com.example.tiffin.data.model.Kitchen
import com.example.tiffin.ui.components.KitchenCard
import com.example.tiffin.ui.viewModel.KitchenUiState
import com.example.tiffin.ui.viewModel.KitchenViewModel

private val PrimaryGreen = Color(0xFF267A4B)
private val LightGreen = Color(0xFFEAF5ED)
private val ScreenBackground = Color(0xFFF8F9F6)
private val DarkText = Color(0xFF252525)
private val GreyText = Color(0xFF777777)

@Composable
fun CenterBox(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
fun MessageWithAction(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(message, textAlign = TextAlign.Center, color = DarkText, fontSize = 16.sp)
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text(actionLabel)
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: KitchenViewModel,
    analytics: Analytics,
    isSubscribed: Boolean,
    onKitchenClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var filterVegOnly by remember { mutableStateOf(false) }

    when (val state = uiState) {
        KitchenUiState.Loading -> CenterBox {
            CircularProgressIndicator(color = PrimaryGreen)
        }
        KitchenUiState.Empty -> CenterBox {
            MessageWithAction(
                message = "No kitchens found near your location.",
                actionLabel = "Refresh",
                onAction = viewModel::loadKitchens
            )
        }
        is KitchenUiState.Error -> CenterBox {
            MessageWithAction(
                message = state.message,
                actionLabel = "Retry",
                onAction = viewModel::loadKitchens
            )
        }
        is KitchenUiState.Success -> {
            LaunchedEffect(state.kitchens.size) {
                analytics.logEvent(
                    "kitchen_list_loaded",
                    mapOf("kitchen_count" to state.kitchens.size)
                )
            }

            val displayedKitchens = remember(state.kitchens, filterVegOnly) {
                if (filterVegOnly) state.kitchens.filter { it.isVeg } else state.kitchens
            }

            Scaffold(
                containerColor = ScreenBackground
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📍 Delivering to Home",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GreyText
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "Kitchens Near You",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DarkText
                                    )
                                }
                                if (isSubscribed) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = LightGreen
                                    ) {
                                        Text(
                                            text = "PLUS ACTIVE",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            color = PrimaryGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Filter Chips
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = !filterVegOnly,
                                    onClick = { filterVegOnly = false },
                                    label = { Text("All Kitchens (${state.kitchens.size})") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = filterVegOnly,
                                    onClick = { filterVegOnly = true },
                                    label = { Text("Pure Veg 🌱") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    if (displayedKitchens.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No kitchens match your filter.", color = GreyText)
                            }
                        }
                    } else {
                        items(displayedKitchens, key = { it.id }) { kitchen ->
                            KitchenCard(
                                kitchen = kitchen,
                                onClick = { onKitchenClick(kitchen.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
