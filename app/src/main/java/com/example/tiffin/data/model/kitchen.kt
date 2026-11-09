package com.example.tiffin.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Kitchen(
    val id: Int,
    val name: String,
    val cuisine: String,
    val pricePerTiffin: Int,
    val isVeg: Boolean,
    val rating: Double,
    val distanceKm: Double,
    val weeklyMenu: Map<String, String>
)