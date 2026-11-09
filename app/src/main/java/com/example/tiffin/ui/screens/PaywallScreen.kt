package com.example.tiffin.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date
import java.util.Locale

private val PrimaryGreen = Color(0xFF267A4B)
private val LightGreen = Color(0xFFEAF5ED)
private val DarkText = Color(0xFF252525)
private val GreyText = Color(0xFF666666)
private val GoldAccent = Color(0xFFB78103)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onPurchase: () -> Unit,
    onClose: () -> Unit
) {
    // 24 hours from now
    val renewalDate = remember {
        val targetMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
        SimpleDateFormat("d MMMM yyyy 'at' h:mm a", Locale.getDefault()).format(Date(targetMillis))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Surface(
                shape = CircleShape,
                color = LightGreen,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🍱", fontSize = 36.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF3D9)
            ) {
                Text(
                    text = "TIFFIN PLUS",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Unlock Daily Home Cooked Meals",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = DarkText
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Get priority daily delivery slots, complete weekly menus, and chef's daily specials.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = GreyText
            )

            Spacer(Modifier.height(24.dp))

            // Feature Highlights
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    PaywallFeatureRow("Delivery slots reserved daily in advance")
                    Spacer(Modifier.height(10.dp))
                    PaywallFeatureRow("Full upcoming weekly menu breakdown")
                    Spacer(Modifier.height(10.dp))
                    PaywallFeatureRow("Zero delivery fee from verified home kitchens")
                    Spacer(Modifier.height(10.dp))
                    PaywallFeatureRow("Cancel anytime with zero penalty")
                }
            }

            Spacer(Modifier.height(24.dp))

            // Pricing Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Special Trial Offer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹1",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreen
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "for your first 24 hours",
                            fontSize = 14.sp,
                            color = DarkText,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = PrimaryGreen.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "₹1 is charged now. On $renewalDate (24 hours later), your plan automatically renews at ₹249 per month.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = DarkText,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Demo build: no real payment is made.",
                style = MaterialTheme.typography.bodySmall,
                color = GreyText
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onPurchase,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Start for ₹1",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onClose) {
                Text(
                    text = "Not now",
                    color = GreyText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PaywallFeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = DarkText
        )
    }
}
