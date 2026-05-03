package com.bulgariaexplorer.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.UserRepository

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val poiRepository: PoiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(userRepository, poiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
