package com.bulgariaexplorer.app.ui.visits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.VisitRepository

class MyVisitsViewModelFactory(
    private val visitRepository: VisitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyVisitsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyVisitsViewModel(visitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
