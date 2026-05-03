package com.bulgariaexplorer.app.ui.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.MissionRepository

class MissionsViewModelFactory(
    private val repository: MissionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MissionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
