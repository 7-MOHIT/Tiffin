package com.example.tiffin.ui.components

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

private val PrimaryGreen = Color(0xFF267A4B)
private val LightGreen = Color(0xFFEAF5ED)
private val DarkText = Color(0xFF252525)
private val GreyText = Color(0xFF777777)
private val LockBg = Color(0xFFF3F3F3)
private val LockText = Color(0xFF888888)

@Composable
fun WeeklyMenuCard(
    day: String,
    meal: String,
    dayNumber: Int,
    isLocked: Boolean = false,
    onLockedClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isLocked && onLockedClick != null) {
                    Modifier.clickable { onLockedClick() }
                } else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color(0xFFFAFAFA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLocked) 1.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = if (isLocked) LockBg else LightGreen
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%02d".format(dayNumber),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) LockText else PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = day,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) GreyText else DarkText
                    )
                    if (isLocked) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF0D4)
                        ) {
                            Text(
                                text = "PLUS ONLY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E6500),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = if (isLocked) "🔒 Locked • Tap to unlock weekly menu" else meal,
                    fontSize = 13.sp,
                    color = if (isLocked) Color(0xFF999999) else GreyText,
                    fontWeight = if (isLocked) FontWeight.Medium else FontWeight.Normal,
                    lineHeight = 19.sp
                )
            }

            Text(
                text = if (isLocked) "🔒" else "🍱",
                fontSize = 24.sp
            )
        }
    }
}
