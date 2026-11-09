package com.example.tiffin.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiffin.data.model.Kitchen
import com.example.tiffin.data.repository.KitchenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KitchenUiState(
    val isLoading: Boolean = true,
    val kitchens: List<Kitchen> = emptyList(),
    val error: String? = null
)

class KitchenViewModel(
    private val repository: KitchenRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(KitchenUiState())
    val uiState: StateFlow<KitchenUiState> = _uiState.asStateFlow()

    init {
        loadKitchens()
    }

    private fun loadKitchens() {
        viewModelScope.launch {
            _uiState.value = KitchenUiState(isLoading = true)
            repository.getKitchens().fold(onSuccess = { kitchens ->
                _uiState.value = KitchenUiState(
                    isLoading = false, kitchens = kitchens
                )
            }, onFailure = { exception ->
                _uiState.value = KitchenUiState(
                    isLoading = false, error = exception.message ?: "Something went wrong"
                )
            })
        }
    }
}