package com.bulgariaexplorer.app.ui.admin.achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminAchievementRequest
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminAchievementResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminAchievementsViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    sealed class AchievementsState {
        data object Loading : AchievementsState()
        data class Success(val achievements: List<AdminAchievementResponse>) : AchievementsState()
        data class Error(val message: String) : AchievementsState()
    }

    sealed class FormState {
        data object Idle : FormState()
        data object Loading : FormState()
        data class Success(val message: String) : FormState()
        data class Error(val message: String) : FormState()
    }

    private val _achievementsState = MutableLiveData<AchievementsState>()
    val achievementsState: LiveData<AchievementsState> = _achievementsState

    private val _formState = MutableLiveData<FormState>(FormState.Idle)
    val formState: LiveData<FormState> = _formState

    fun loadAchievements() {
        _achievementsState.value = AchievementsState.Loading
        viewModelScope.launch {
            adminRepository.getAllAchievements()
                .onSuccess { _achievementsState.value = AchievementsState.Success(it) }
                .onFailure { _achievementsState.value = AchievementsState.Error(it.message ?: "Грешка") }
        }
    }

    fun createAchievement(request: AdminAchievementRequest) {
        _formState.value = FormState.Loading
        viewModelScope.launch {
            adminRepository.createAchievement(request)
                .onSuccess {
                    _formState.value = FormState.Success("Постижението е създадено")
                    loadAchievements()
                }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }

    fun updateAchievement(achievementId: Long, request: AdminAchievementRequest) {
        _formState.value = FormState.Loading
        viewModelScope.launch {
            adminRepository.updateAchievement(achievementId, request)
                .onSuccess {
                    _formState.value = FormState.Success("Постижението е обновено")
                    loadAchievements()
                }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }

    fun deleteAchievement(achievementId: Long) {
        viewModelScope.launch {
            adminRepository.deleteAchievement(achievementId)
                .onSuccess {
                    _formState.value = FormState.Success("Постижението е изтрито")
                    loadAchievements()
                }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }
}
