package com.bulgariaexplorer.app.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.FavoriteResponse
import com.bulgariaexplorer.app.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {

    private val _favoritesState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> = _favoritesState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = FavoritesState.Loading
            val result = favoriteRepository.getFavorites()
            Log.d("FavoritesViewModel", "Result: $result")
            _favoritesState.value = if (result.isSuccess) {
                val favorites = result.getOrNull()!!
                Log.d("FavoritesViewModel", "Favorites count: ${favorites.size}")
                favorites.forEachIndexed { index, fav ->
                    Log.d("FavoritesViewModel", "Favorite $index: id=${fav.id}, poiId=${fav.poiId}, title=${fav.poiTitle}")
                }
                FavoritesState.Success(favorites)
            } else {
                Log.e("FavoritesViewModel", "Error: ${result.exceptionOrNull()?.message}")
                FavoritesState.Error(result.exceptionOrNull()?.message ?: "Failed to load favorites")
            }
        }
    }

    sealed class FavoritesState {
        object Loading : FavoritesState()
        data class Success(val favorites: List<FavoriteResponse>) : FavoritesState()
        data class Error(val message: String) : FavoritesState()
    }
}
