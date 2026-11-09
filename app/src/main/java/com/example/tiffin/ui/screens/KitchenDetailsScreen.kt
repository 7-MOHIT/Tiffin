package com.example.tiffin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tiffin.analytics.Analytics
import com.example.tiffin.data.model.Kitchen
import com.example.tiffin.ui.components.InfoTile
import com.example.tiffin.ui.components.WeeklyMenuCard
import com.example.tiffin.ui.viewModel.KitchenUiState
import com.example.tiffin.ui.viewModel.KitchenViewModel

private val PrimaryGreen = Color(0xFF267A4B)
private val LightGreen = Color(0xFFEAF5ED)
private val DarkText = Color(0xFF252525)
private val GreyText = Color(0xFF777777)
private val ScreenBackground = Color(0xFFF8F9F6)

private val days = listOf(
    "Mon" to "Monday",
    "Tue" to "Tuesday",
    "Wed" to "Wednesday",
    "Thu" to "Thursday",
    "Fri" to "Friday",
    "Sat" to "Saturday",
    "Sun" to "Sunday"
)

@Composable
fun KitchenDetailsScreen(
    kitchenId: Int,
    viewModel: KitchenViewModel,
    analytics: Analytics,
    isSubscribed: Boolean,
    onBackClick: () -> Unit,
    onSubscribeClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        KitchenUiState.Loading -> CenterBox {
            CircularProgressIndicator(color = PrimaryGreen)
        }
        KitchenUiState.Empty -> CenterBox {
            MessageWithAction("Kitchen not found", "Back", onBackClick)
        }
        is KitchenUiState.Error -> CenterBox {
            MessageWithAction(state.message, "Retry", viewModel::loadKitchens)
        }
        is KitchenUiState.Success -> {
            val kitchen = state.kitchens.firstOrNull { it.id == kitchenId }
            if (kitchen == null) {
                CenterBox { MessageWithAction("Kitchen not found", "Back", onBackClick) }
            } else {
                LaunchedEffect(kitchen.id) {
                    analytics.logEvent(
                        "kitchen_detail_viewed",
                        mapOf(
                            "kitchen_id" to kitchen.id,
                            "kitchen_name" to kitchen.name,
                            "cuisine" to kitchen.cuisine,
                            "price" to kitchen.pricePerTiffin,
                            "is_veg" to kitchen.isVeg,
                            "rating" to kitchen.rating
                        )
                    )
                }
                KitchenDetailsContent(
                    kitchen = kitchen,
                    isSubscribed = isSubscribed,
                    onBackClick = onBackClick,
                    onSubscribeClick = onSubscribeClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KitchenDetailsContent(
    kitchen: Kitchen,
    isSubscribed: Boolean,
    onBackClick: () -> Unit,
    onSubscribeClick: (Int) -> Unit
) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        kitchen.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBackground,
                    titleContentColor = DarkText
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = { onSubscribeClick(kitchen.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) Color(0xFF2E7D32) else PrimaryGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .height(52.dp)
                ) {
                    Text(
                        text = if (isSubscribed) "Subscribed ✓ (Tiffin Plus Active)" else "Subscribe for ₹1 Trial",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(kitchen.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = DarkText)
            Spacer(Modifier.height(4.dp))
            Text("${kitchen.cuisine} • ${kitchen.distanceKm} km away", fontSize = 14.sp, color = GreyText)

            Spacer(Modifier.height(20.dp))
            Text("Kitchen Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(Modifier.height(12.dp))

            // 2x2 Grid of InfoTiles
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoTile(
                    title = "Rating",
                    value = "★ ${kitchen.rating}",
                    emoji = "⭐",
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                InfoTile(
                    title = "Dietary",
                    value = if (kitchen.isVeg) "Pure Veg" else "Veg & Non-Veg",
                    emoji = if (kitchen.isVeg) "🌱" else "🍗",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoTile(
                    title = "Price",
                    value = "₹${kitchen.pricePerTiffin}",
                    emoji = "💰",
                    subtitle = "per meal",
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                InfoTile(
                    title = "Distance",
                    value = "${kitchen.distanceKm} km",
                    emoji = "📍",
                    subtitle = "approx delivery",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(26.dp))

            // Header for Weekly Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Weekly Menu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSubscribed) LightGreen else Color(0xFFFFF3D9)
                ) {
                    Text(
                        text = if (isSubscribed) "Full Menu Unlocked ✓" else "Monday Free Preview",
                        color = if (isSubscribed) PrimaryGreen else Color(0xFF9A6800),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Info banner explaining the free teaser vs subscription unlock
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSubscribed) LightGreen.copy(alpha = 0.6f) else Color(0xFFFFF9E6)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isSubscribed) "🎉" else "💡", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isSubscribed) {
                            "You have full access to all 7 days of daily rotating home meals and priority delivery slots."
                        } else {
                            "Monday's meal is open for free preview. Subscribe to Tiffin Plus for ₹1 to unlock Tue–Sun menus!"
                        },
                        fontSize = 12.sp,
                        color = if (isSubscribed) DarkText else Color(0xFF7A5200),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            days.forEachIndexed { index, (shortKey, fullDayName) ->
                val isMonday = index == 0
                val isDayLocked = !isSubscribed && !isMonday

                val rawMeal = kitchen.weeklyMenu[fullDayName]
                    ?: kitchen.weeklyMenu[shortKey]
                    ?: "Chef's Special Homestyle Thali"

                WeeklyMenuCard(
                    day = fullDayName,
                    meal = rawMeal,
                    dayNumber = index + 1,
                    isLocked = isDayLocked,
                    onLockedClick = { onSubscribeClick(kitchen.id) }
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
