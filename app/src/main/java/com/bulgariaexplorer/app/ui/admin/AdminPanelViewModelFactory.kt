package com.bulgariaexplorer.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bulgariaexplorer.app.data.repository.AdminRepository

class AdminPanelViewModelFactory(
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminPanelViewModel(adminRepository) as T
    }
}
