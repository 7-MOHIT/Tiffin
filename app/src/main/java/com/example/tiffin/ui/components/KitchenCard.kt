package com.example.tiffin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiffin.data.model.Kitchen

private val Green = Color(0xFF267A4B)
private val LightGreen = Color(0xFFE8F5EC)
private val Cream = Color(0xFFFFF8EE)
private val DarkText = Color(0xFF252525)
private val GreyText = Color(0xFF777777)

@Composable
fun KitchenCard(
    kitchen: Kitchen,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = kitchen.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = kitchen.cuisine, fontSize = 14.sp, color = GreyText
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp), color = Color(0xFFFFF3D9)
                ) {
                    Text(
                        text = "★ ${kitchen.rating}", modifier = Modifier.padding(
                            horizontal = 10.dp, vertical = 7.dp
                        ), color = Color(0xFF9A6800), fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp), color = if (kitchen.isVeg) LightGreen
                    else Color(0xFFFFEEEE)
                ) {
                    Text(
                        text = if (kitchen.isVeg) "● Pure Veg"
                        else "● Veg & Non-Veg", modifier = Modifier.padding(
                            horizontal = 10.dp, vertical = 6.dp
                        ), color = if (kitchen.isVeg) Green
                        else Color(0xFFB33A3A), fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEEEEEE))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Starting from", fontSize = 12.sp, color = GreyText
                    )
                    Text(
                        text = "₹${kitchen.pricePerTiffin}",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Green
                    )
                    Text(
                        text = "per tiffin", fontSize = 12.sp, color = GreyText
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp), color = Green
                ) {
                    Text(
                        text = "View Menu  →", modifier = Modifier.padding(
                            horizontal = 14.dp, vertical = 11.dp
                        ), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp
                    )
                }
            }
        }
    }
}
