package com.bulgariaexplorer.app.ui.poi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.PoiRepository

class PoiViewModelFactory(private val poiRepository: PoiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoiViewModel(poiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
