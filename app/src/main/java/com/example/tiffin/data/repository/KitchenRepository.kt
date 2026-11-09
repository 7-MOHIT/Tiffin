package com.example.tiffin.data.repository

import android.content.Context
import com.example.tiffin.data.model.Kitchen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.CancellationException

class KitchenRepository(
    private val context: Context
) {
    private val appContext = context.applicationContext
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getKitchens(): Result<List<Kitchen>> = withContext(Dispatchers.IO) {
        try {
            val text =
                appContext.assets.open("kitchens.json").bufferedReader().use { it.readText() }
            Result.success(json.decodeFromString<List<Kitchen>>(text))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}