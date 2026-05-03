package com.bulgariaexplorer.app.ui.admin.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.AdminRepository

class AdminMissionsViewModelFactory(
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminMissionsViewModel(adminRepository) as T
    }
}
