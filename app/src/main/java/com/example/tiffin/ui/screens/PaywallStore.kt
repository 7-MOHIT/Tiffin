package com.example.tiffin.ui.screens

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class PaywallState(
    val launchCount: Int,
    val paid: Boolean,
    val paidAt: Long,
    val autoPaywallShown: Boolean = false
)

private val Context.dataStore by preferencesDataStore("tiffin_prefs")

class PaywallStore(context: Context) {
    private val ds = context.applicationContext.dataStore
    private val LAUNCH = intPreferencesKey("launch_count")
    private val PAID = booleanPreferencesKey("paid")
    private val PAID_AT = longPreferencesKey("paid_at")
    private val AUTO_SHOWN = booleanPreferencesKey("auto_paywall_shown")

    val state: Flow<PaywallState> = ds.data.map {
        PaywallState(
            launchCount = it[LAUNCH] ?: 0,
            paid = it[PAID] ?: false,
            paidAt = it[PAID_AT] ?: 0L,
            autoPaywallShown = it[AUTO_SHOWN] ?: false
        )
    }

    suspend fun incrementLaunch() = ds.edit { it[LAUNCH] = (it[LAUNCH] ?: 0) + 1 }
    suspend fun markPaid(now: Long) = ds.edit {
        it[PAID] = true
        it[PAID_AT] = now
    }
    suspend fun markAutoPaywallShown() = ds.edit {
        it[AUTO_SHOWN] = true
    }
}
