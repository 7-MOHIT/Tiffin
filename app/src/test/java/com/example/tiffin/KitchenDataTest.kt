package com.example.tiffin

import com.example.tiffin.data.model.Kitchen
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class KitchenDataTest {

    private val sampleJson = """
        [
          {
            "id": 1,
            "name": "Sharma Ji Ka Tiffin",
            "cuisine": "North Indian",
            "pricePerTiffin": 80,
            "isVeg": true,
            "rating": 4.5,
            "distanceKm": 0.8,
            "weeklyMenu": {
              "Monday": "Aloo Paratha, Curd, Pickle",
              "Tuesday": "Rajma Chawal",
              "Wednesday": "Dal Makhani, Roti, Rice"
            }
          },
          {
            "id": 2,
            "name": "Amma's South Kitchen",
            "cuisine": "South Indian",
            "pricePerTiffin": 90,
            "isVeg": true,
            "rating": 4.7,
            "distanceKm": 1.8,
            "weeklyMenu": {
              "Monday": "Idli, Sambar, Chutney",
              "Tuesday": "Masala Dosa"
            }
          }
        ]
    """.trimIndent()

    @Test
    fun testKitchenJsonParsing() {
        val json = Json { ignoreUnknownKeys = true }
        val kitchens: List<Kitchen> = json.decodeFromString(sampleJson)

        assertEquals(2, kitchens.size)
        assertEquals("Sharma Ji Ka Tiffin", kitchens[0].name)
        assertTrue(kitchens[0].isVeg)
        assertEquals(4.5, kitchens[0].rating, 0.01)
        assertEquals("Aloo Paratha, Curd, Pickle", kitchens[0].weeklyMenu["Monday"])
    }

    @Test
    fun testKitchenSortingByDistance() {
        val json = Json { ignoreUnknownKeys = true }
        val kitchens: List<Kitchen> = json.decodeFromString(sampleJson)
        val sorted = kitchens.sortedBy { it.distanceKm }

        assertEquals(1, sorted[0].id)
        assertEquals(2, sorted[1].id)
        assertTrue(sorted[0].distanceKm < sorted[1].distanceKm)
    }

    @Test
    fun testMenuLookupWithDayNames() {
        val json = Json { ignoreUnknownKeys = true }
        val kitchens: List<Kitchen> = json.decodeFromString(sampleJson)
        val kitchen = kitchens[0]

        val monMeal = kitchen.weeklyMenu["Monday"] ?: kitchen.weeklyMenu["Mon"]
        assertNotNull(monMeal)
        assertEquals("Aloo Paratha, Curd, Pickle", monMeal)
    }
}
