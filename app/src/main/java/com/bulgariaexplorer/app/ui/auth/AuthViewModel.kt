package com.bulgariaexplorer.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.auth.AuthResponse
import com.bulgariaexplorer.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState

    private val _registerState = MutableLiveData<AuthState>()
    val registerState: LiveData<AuthState> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            _loginState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            val result = authRepository.register(username, email, password)
            _registerState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val authResponse: AuthResponse) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
