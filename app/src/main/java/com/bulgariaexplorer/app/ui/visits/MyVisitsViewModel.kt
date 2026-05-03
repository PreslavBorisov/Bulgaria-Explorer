package com.bulgariaexplorer.app.ui.visits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.VisitResponse
import com.bulgariaexplorer.app.data.repository.VisitRepository
import kotlinx.coroutines.launch

class MyVisitsViewModel(private val visitRepository: VisitRepository) : ViewModel() {

    private val _visitsState = MutableLiveData<VisitsState>()
    val visitsState: LiveData<VisitsState> = _visitsState

    init {
        loadVisits()
    }

    fun loadVisits() {
        viewModelScope.launch {
            _visitsState.value = VisitsState.Loading
            val result = visitRepository.getMyVisits()
            _visitsState.value = if (result.isSuccess) {
                val visits = result.getOrNull()!!
                VisitsState.Success(visits)
            } else {
                VisitsState.Error(result.exceptionOrNull()?.message ?: "Неуспешно зареждане на посещенията")
            }
        }
    }

    sealed class VisitsState {
        object Loading : VisitsState()
        data class Success(val visits: List<VisitResponse>) : VisitsState()
        data class Error(val message: String) : VisitsState()
    }
}
