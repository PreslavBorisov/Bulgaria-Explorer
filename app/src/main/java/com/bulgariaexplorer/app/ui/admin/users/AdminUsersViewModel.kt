package com.bulgariaexplorer.app.ui.admin.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminUserResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminUsersViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    sealed class UsersState {
        data object Loading : UsersState()
        data class Success(val users: List<AdminUserResponse>) : UsersState()
        data class Error(val message: String) : UsersState()
    }

    sealed class ActionState {
        data object Idle : ActionState()
        data object Loading : ActionState()
        data class Success(val message: String) : ActionState()
        data class Error(val message: String) : ActionState()
    }

    private val _usersState = MutableLiveData<UsersState>()
    val usersState: LiveData<UsersState> = _usersState

    private val _actionState = MutableLiveData<ActionState>(ActionState.Idle)
    val actionState: LiveData<ActionState> = _actionState

    fun loadUsers() {
        _usersState.value = UsersState.Loading
        viewModelScope.launch {
            adminRepository.getAllUsers()
                .onSuccess { _usersState.value = UsersState.Success(it) }
                .onFailure { _usersState.value = UsersState.Error(it.message ?: "Грешка") }
        }
    }

    fun changeUserRole(userId: Long, newRole: String) {
        _actionState.value = ActionState.Loading
        viewModelScope.launch {
            adminRepository.changeUserRole(userId, newRole)
                .onSuccess {
                    _actionState.value = ActionState.Success("Ролята е променена")
                    loadUsers()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Грешка")
                }
        }
    }

    fun deleteUser(userId: Long) {
        _actionState.value = ActionState.Loading
        viewModelScope.launch {
            adminRepository.deleteUser(userId)
                .onSuccess {
                    _actionState.value = ActionState.Success("Потребителят е изтрит")
                    loadUsers()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Грешка")
                }
        }
    }
}
