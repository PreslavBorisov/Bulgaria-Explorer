package com.bulgariaexplorer.app.ui.missions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.MissionProgressResponse
import com.bulgariaexplorer.app.data.repository.MissionRepository
import kotlinx.coroutines.launch

class MissionsViewModel(private val repository: MissionRepository) : ViewModel() {

    private val _missionsState = MutableLiveData<MissionsState>()
    val missionsState: LiveData<MissionsState> = _missionsState

    init {
        loadMissions()
    }

    fun loadMissions() {
        viewModelScope.launch {
            _missionsState.value = MissionsState.Loading
            val result = repository.getMyMissions()
            _missionsState.value = if (result.isSuccess) {
                MissionsState.Success(result.getOrNull()!!)
            } else {
                MissionsState.Error(result.exceptionOrNull()?.message ?: "Failed to load missions")
            }
        }
    }

    sealed class MissionsState {
        object Loading : MissionsState()
        data class Success(val missions: List<MissionProgressResponse>) : MissionsState()
        data class Error(val message: String) : MissionsState()
    }
}
