package com.example.tiffin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiffin.ui.components.InfoTile
import com.example.tiffin.ui.components.WeeklyMenuCard
import com.example.tiffin.ui.viewModel.KitchenViewModel

private val PrimaryGreen = Color(0xFF267A4B)
private val LightGreen = Color(0xFFEAF5ED)
private val DarkText = Color(0xFF252525)
private val GreyText = Color(0xFF777777)
private val ScreenBackground = Color(0xFFF8F9F6)

@Composable
fun KitchenDetailsScreen(
    kitchenId: Int,
    viewModel: KitchenViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val kitchen = uiState.kitchens.firstOrNull {
        it.id == kitchenId
    }
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        }

        uiState.error != null -> {
            Text(
                text = uiState.error ?: "Something went wrong"
            )
        }

        kitchen == null -> {
            Text("Kitchen not found")
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScreenBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clickable { onBackClick() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 11.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "←",
                            fontSize = 22.sp,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Back to Kitchens",
                            color = DarkText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = kitchen.name,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Fresh homemade food, made with care",
                    fontSize = 14.sp,
                    color = GreyText
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Kitchen Information",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoTile(
                        title = "Cuisine",
                        value = kitchen.cuisine,
                        emoji = "🍛",
                        modifier = Modifier.weight(1f)
                    )

                    InfoTile(
                        title = "Price",
                        value = "₹${kitchen.pricePerTiffin}",
                        subtitle = "Per tiffin",
                        emoji = "💰",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoTile(
                        title = "Food Type",
                        value = if (kitchen.isVeg) "Pure Veg" else "Non-Veg",
                        emoji = "🥗",
                        modifier = Modifier.weight(1f)
                    )

                    InfoTile(
                        title = "Rating",
                        value = "${kitchen.rating} / 5",
                        emoji = "⭐",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Weekly Menu",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "See what's cooking throughout the week",
                    fontSize = 13.sp,
                    color = GreyText
                )
                Spacer(modifier = Modifier.height(16.dp))
                val days = listOf(
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday",
                    "Sunday"
                )
                days.forEachIndexed { index, day ->
                    WeeklyMenuCard(
                        day = day,
                        meal = kitchen.weeklyMenu[day]
                            ?: "Menu not available",
                        dayNumber = index + 1
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}




