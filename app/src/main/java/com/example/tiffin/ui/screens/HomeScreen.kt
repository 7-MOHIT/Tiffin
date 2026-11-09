package com.example.tiffin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tiffin.ui.viewModel.KitchenViewModel

@Composable
fun HomeScreen(
    viewModel: KitchenViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(text = uiState.error?: "Something went wrong")
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Available Kitchens",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                items(
                    items = uiState.kitchens,
                    key = { it.id }
                ) { kitchen ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = kitchen.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(text = kitchen.cuisine)
                            Text(
                                text = "₹${kitchen.pricePerTiffin} / Tiffin"
                            )
                            Text(
                                text = if (kitchen.isVeg) "Veg" else "Non-Veg"
                            )
                            Text(
                                text = "⭐ ${kitchen.rating}"
                            )
                        }
                    }
                }
            }
        }
    }
}