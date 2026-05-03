package com.bulgariaexplorer.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.admin.DashboardStatsResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminPanelViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    sealed class StatsState {
        data object Loading : StatsState()
        data class Success(val stats: DashboardStatsResponse) : StatsState()
        data class Error(val message: String) : StatsState()
    }

    private val _statsState = MutableLiveData<StatsState>()
    val statsState: LiveData<StatsState> = _statsState

    fun loadStats() {
        _statsState.value = StatsState.Loading
        viewModelScope.launch {
            adminRepository.getDashboardStats()
                .onSuccess { _statsState.value = StatsState.Success(it) }
                .onFailure { _statsState.value = StatsState.Error(it.message ?: "Грешка") }
        }
    }
}
