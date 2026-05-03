package com.bulgariaexplorer.app.ui.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.AdminRepository

class AdminUsersViewModelFactory(
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminUsersViewModel(adminRepository) as T
    }
}
