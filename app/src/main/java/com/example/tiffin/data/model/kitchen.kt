package com.example.tiffin.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Kitchen(
    val id: Int,
    val name: String
)