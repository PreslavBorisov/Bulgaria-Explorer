package com.bulgariaexplorer.app.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.FavoriteRepository

class FavoritesViewModelFactory(
    private val favoriteRepository: FavoriteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoriteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
