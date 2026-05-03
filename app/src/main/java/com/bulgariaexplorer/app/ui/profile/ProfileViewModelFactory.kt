package com.bulgariaexplorer.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
import com.bulgariaexplorer.app.data.repository.UserRepository

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository, leaderboardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
