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
    suspend fun getKitchens(): Result<List<Kitchen>> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString =
                    context.assets.open("kitchens.json").bufferedReader().use { it.readText() }
                val kitchens = Json.decodeFromString<List<Kitchen>>(
                    jsonString
                )
                Result.success(kitchens)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}