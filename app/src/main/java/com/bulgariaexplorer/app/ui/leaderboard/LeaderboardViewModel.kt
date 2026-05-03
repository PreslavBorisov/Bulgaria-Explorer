package com.bulgariaexplorer.app.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.LeaderboardResponse
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
import kotlinx.coroutines.launch

class LeaderboardViewModel(private val repository: LeaderboardRepository) : ViewModel() {

    private val _leaderboardState = MutableLiveData<LeaderboardState>()
    val leaderboardState: LiveData<LeaderboardState> = _leaderboardState

    private val _myRankState = MutableLiveData<MyRankState>()
    val myRankState: LiveData<MyRankState> = _myRankState

    init {
        loadLeaderboard()
        loadMyRank()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _leaderboardState.value = LeaderboardState.Loading
            val result = repository.getTop10()
            _leaderboardState.value = if (result.isSuccess) {
                LeaderboardState.Success(result.getOrNull()!!)
            } else {
                LeaderboardState.Error(result.exceptionOrNull()?.message ?: "Failed to load leaderboard")
            }
        }
    }

    private fun loadMyRank() {
        viewModelScope.launch {
            val result = repository.getMe()
            _myRankState.value = if (result.isSuccess) {
                MyRankState.Success(result.getOrNull()!!)
            } else {
                MyRankState.Error(result.exceptionOrNull()?.message ?: "Failed to load rank")
            }
        }
    }

    sealed class LeaderboardState {
        object Loading : LeaderboardState()
        data class Success(val entries: List<LeaderboardResponse>) : LeaderboardState()
        data class Error(val message: String) : LeaderboardState()
    }

    sealed class MyRankState {
        data class Success(val entry: LeaderboardResponse) : MyRankState()
        data class Error(val message: String) : MyRankState()
    }
}
