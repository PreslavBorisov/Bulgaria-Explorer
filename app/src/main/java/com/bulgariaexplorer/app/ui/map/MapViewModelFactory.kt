package com.bulgariaexplorer.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.PoiRepository

class MapViewModelFactory(
    private val poiRepository: PoiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(poiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
