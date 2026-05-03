package com.bulgariaexplorer.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.AchievementResponse
import com.bulgariaexplorer.app.data.remote.dto.UserResponse
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
import com.bulgariaexplorer.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> = _userState

    private val _achievementsState = MutableLiveData<AchievementsState>()
    val achievementsState: LiveData<AchievementsState> = _achievementsState

    init {
        loadProfile()
        loadAchievements()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            val result = userRepository.getCurrentUser()
            _userState.value = if (result.isSuccess) {
                UserState.Success(result.getOrNull()!!)
            } else {
                UserState.Error(result.exceptionOrNull()?.message ?: "Failed to load profile")
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            _achievementsState.value = AchievementsState.Loading
            val result = leaderboardRepository.getMyAchievements()
            _achievementsState.value = if (result.isSuccess) {
                AchievementsState.Success(result.getOrNull()!!)
            } else {
                AchievementsState.Error(result.exceptionOrNull()?.message ?: "Failed to load achievements")
            }
        }
    }

    sealed class UserState {
        object Loading : UserState()
        data class Success(val user: UserResponse) : UserState()
        data class Error(val message: String) : UserState()
    }

    sealed class AchievementsState {
        object Loading : AchievementsState()
        data class Success(val achievements: List<AchievementResponse>) : AchievementsState()
        data class Error(val message: String) : AchievementsState()
    }
}
