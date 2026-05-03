package com.bulgariaexplorer.app.ui.admin.missions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminMissionRequest
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminMissionResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminMissionsViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    sealed class MissionsState {
        data object Loading : MissionsState()
        data class Success(val missions: List<AdminMissionResponse>) : MissionsState()
        data class Error(val message: String) : MissionsState()
    }

    sealed class FormState {
        data object Idle : FormState()
        data object Loading : FormState()
        data class Success(val message: String) : FormState()
        data class Error(val message: String) : FormState()
    }

    private val _missionsState = MutableLiveData<MissionsState>()
    val missionsState: LiveData<MissionsState> = _missionsState

    private val _formState = MutableLiveData<FormState>(FormState.Idle)
    val formState: LiveData<FormState> = _formState

    fun loadMissions() {
        _missionsState.value = MissionsState.Loading
        viewModelScope.launch {
            adminRepository.getAllMissions()
                .onSuccess { _missionsState.value = MissionsState.Success(it) }
                .onFailure { _missionsState.value = MissionsState.Error(it.message ?: "Грешка") }
        }
    }

    fun createMission(request: AdminMissionRequest) {
        _formState.value = FormState.Loading
        viewModelScope.launch {
            adminRepository.createMission(request)
                .onSuccess {
                    _formState.value = FormState.Success("Мисията е създадена")
                    loadMissions()
                }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }

    fun updateMission(missionId: Long, request: AdminMissionRequest) {
        _formState.value = FormState.Loading
        viewModelScope.launch {
            adminRepository.updateMission(missionId, request)
                .onSuccess {
                    _formState.value = FormState.Success("Мисията е обновена")
                    loadMissions()
                }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }

    fun deleteMission(missionId: Long) {
        viewModelScope.launch {
            adminRepository.deleteMission(missionId)
                .onSuccess {
                    _formState.value = FormState.Success("Мисията е изтрита")
                    loadMissions()
                }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }
}
