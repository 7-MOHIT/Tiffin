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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    "Mon" to "Monday", "Tue" to "Tuesday", "Wed" to "Wednesday",
    "Thu" to "Thursday", "Fri" to "Friday", "Sat" to "Saturday", "Sun" to "Sunday"
)
@Composable
fun KitchenDetailsScreen(
    kitchenId: Int,
    viewModel: KitchenViewModel,
    onBackClick: () -> Unit,
    onSubscribeClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        KitchenUiState.Loading -> CenterBox { CircularProgressIndicator(color = PrimaryGreen) }
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
                KitchenDetailsContent(kitchen, onBackClick, onSubscribeClick)
            }
        }
    }
}




@Composable
private fun KitchenDetailsContent(
    kitchen: Kitchen,
    onBackClick: () -> Unit,
    onSubscribeClick: (Int) -> Unit
) {
    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = { onSubscribeClick(kitchen.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .height(52.dp)
                ) {
                    Text("Subscribe", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            Spacer(Modifier.height(24.dp))
            Text(kitchen.name, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = DarkText)
            Text("${kitchen.distanceKm} km away", fontSize = 14.sp, color = GreyText)

            Spacer(Modifier.height(24.dp))
            Text("Kitchen Information", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(Modifier.height(14.dp))

            Spacer(Modifier.height(28.dp))
            Text("Weekly Menu", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(Modifier.height(16.dp))
            days.forEachIndexed { index, (key, label) ->
                WeeklyMenuCard(
                    day = label,
                    meal = kitchen.weeklyMenu[key] ?: "Menu not available",
                    dayNumber = index + 1
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

