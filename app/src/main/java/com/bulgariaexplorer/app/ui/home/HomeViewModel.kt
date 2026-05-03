package com.bulgariaexplorer.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.remote.dto.UserResponse
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val poiRepository: PoiRepository
) : ViewModel() {

    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> = _userState

    private val _featuredPoisState = MutableLiveData<PoiState>()
    val featuredPoisState: LiveData<PoiState> = _featuredPoisState

    init {
        loadUserData()
        loadFeaturedPois()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            val result = userRepository.getCurrentUser()
            _userState.value = if (result.isSuccess) {
                UserState.Success(result.getOrNull()!!)
            } else {
                UserState.Error(result.exceptionOrNull()?.message ?: "Failed to load user")
            }
        }
    }

    private fun loadFeaturedPois() {
        viewModelScope.launch {
            _featuredPoisState.value = PoiState.Loading
            val result = poiRepository.getAllPois()
            _featuredPoisState.value = if (result.isSuccess) {
                // Get first 5 POIs as featured
                val pois = result.getOrNull()!!.take(5)
                PoiState.Success(pois)
            } else {
                PoiState.Error(result.exceptionOrNull()?.message ?: "Failed to load POIs")
            }
        }
    }

    fun refresh() {
        loadUserData()
        loadFeaturedPois()
    }

    sealed class UserState {
        object Loading : UserState()
        data class Success(val user: UserResponse) : UserState()
        data class Error(val message: String) : UserState()
    }

    sealed class PoiState {
        object Loading : PoiState()
        data class Success(val pois: List<PoiResponse>) : PoiState()
        data class Error(val message: String) : PoiState()
    }
}
