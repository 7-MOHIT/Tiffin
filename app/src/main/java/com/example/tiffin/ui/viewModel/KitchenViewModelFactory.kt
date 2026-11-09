package com.example.tiffin.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tiffin.data.repository.KitchenRepository

class KitchenViewModelFactory(
    private val repository: KitchenRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(KitchenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return KitchenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}