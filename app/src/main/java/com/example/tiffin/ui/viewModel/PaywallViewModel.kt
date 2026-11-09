package com.example.tiffin.ui.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiffin.analytics.Analytics
import com.example.tiffin.ui.screens.PaywallState
import com.example.tiffin.ui.screens.PaywallStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val store: PaywallStore,
    private val analytics: Analytics,
    handle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<PaywallState?> =
        store.state.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // SavedStateHandle ensures incrementLaunch is executed once per app cold start,
        // preventing double counts on configuration changes/screen rotations.
        if (handle.get<Boolean>("counted") != true) {
            handle["counted"] = true
            viewModelScope.launch { store.incrementLaunch() }
        }
    }

    fun markAutoPaywallShown() {
        viewModelScope.launch { store.markAutoPaywallShown() }
    }

    fun purchase(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            store.markPaid(now)
            analytics.logEvent(
                "subscription_completed",
                mapOf(
                    "trial_price" to 1,
                    "recurring_price" to 249,
                    "currency" to "INR",
                    "plan" to "tiffin_plus_monthly",
                    "purchased_at" to now
                )
            )
            onSuccess()
        }
    }
}
