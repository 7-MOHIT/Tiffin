package com.example.tiffin.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiffin.data.model.Kitchen
import com.example.tiffin.data.repository.KitchenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface KitchenUiState {
    data object Loading : KitchenUiState
    data object Empty : KitchenUiState
    data class Error(val message: String) : KitchenUiState
    data class Success(val kitchens: List<Kitchen>) : KitchenUiState
}

class KitchenViewModel(
    private val repository: KitchenRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<KitchenUiState>(KitchenUiState.Loading)
    val uiState: StateFlow<KitchenUiState> = _uiState.asStateFlow()

    init {
        loadKitchens()
    }

     fun loadKitchens() {
        viewModelScope.launch {

            _uiState.value = KitchenUiState.Loading
            delay(600)
            repository.getKitchens().fold(
                onSuccess = { list ->
                    _uiState.value =
                        if (list.isEmpty()) KitchenUiState.Empty
                        else KitchenUiState.Success(list.sortedBy { it.distanceKm })
                },
                onFailure = {
                    _uiState.value =
                        KitchenUiState.Error("Couldn't load kitchens. Please try again.")
                })
        }
    }
}