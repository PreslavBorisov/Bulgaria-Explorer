package com.bulgariaexplorer.app.ui.poi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.FavoriteRepository
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.VisitRepository

class PoiDetailsViewModelFactory(
    private val poiRepository: PoiRepository,
    private val favoriteRepository: FavoriteRepository,
    private val visitRepository: VisitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoiDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoiDetailsViewModel(poiRepository, favoriteRepository, visitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
