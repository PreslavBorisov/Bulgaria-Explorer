package com.bulgariaexplorer.app.ui.admin.pois

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.CategoryResponse
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminPoiRequest
import com.bulgariaexplorer.app.data.repository.AdminRepository
import kotlinx.coroutines.launch

class AdminPoisViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    sealed class PoisState {
        data object Loading : PoisState()
        data class Success(val pois: List<PoiResponse>) : PoisState()
        data class Error(val message: String) : PoisState()
    }

    sealed class ActionState {
        data object Idle : ActionState()
        data class Success(val message: String) : ActionState()
        data class Error(val message: String) : ActionState()
    }

    sealed class FormState {
        data object Idle : FormState()
        data object Loading : FormState()
        data class Success(val message: String) : FormState()
        data class Error(val message: String) : FormState()
    }

    sealed class CategoriesState {
        data object Loading : CategoriesState()
        data class Success(val categories: List<CategoryResponse>) : CategoriesState()
        data class Error(val message: String) : CategoriesState()
    }

    private val _poisState = MutableLiveData<PoisState>()
    val poisState: LiveData<PoisState> = _poisState

    private val _actionState = MutableLiveData<ActionState>(ActionState.Idle)
    val actionState: LiveData<ActionState> = _actionState

    private val _formState = MutableLiveData<FormState>(FormState.Idle)
    val formState: LiveData<FormState> = _formState

    private val _categoriesState = MutableLiveData<CategoriesState>()
    val categoriesState: LiveData<CategoriesState> = _categoriesState

    fun loadPois() {
        _poisState.value = PoisState.Loading
        viewModelScope.launch {
            adminRepository.getAllPois()
                .onSuccess { _poisState.value = PoisState.Success(it) }
                .onFailure { _poisState.value = PoisState.Error(it.message ?: "Грешка") }
        }
    }

    fun loadCategories() {
        _categoriesState.value = CategoriesState.Loading
        viewModelScope.launch {
            adminRepository.getCategories()
                .onSuccess { _categoriesState.value = CategoriesState.Success(it) }
                .onFailure { _categoriesState.value = CategoriesState.Error(it.message ?: "Грешка") }
        }
    }

    fun createPoi(request: AdminPoiRequest) {
        _formState.value = FormState.Loading
        viewModelScope.launch {
            adminRepository.createPoi(request)
                .onSuccess { _formState.value = FormState.Success("Обектът е създаден") }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }

    fun updatePoi(poiId: Long, request: AdminPoiRequest) {
        _formState.value = FormState.Loading
        viewModelScope.launch {
            adminRepository.updatePoi(poiId, request)
                .onSuccess { _formState.value = FormState.Success("Обектът е обновен") }
                .onFailure { _formState.value = FormState.Error(it.message ?: "Грешка") }
        }
    }

    fun deletePoi(poiId: Long) {
        viewModelScope.launch {
            adminRepository.deletePoi(poiId)
                .onSuccess {
                    _actionState.value = ActionState.Success("Обектът е изтрит")
                    loadPois()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Грешка")
                }
        }
    }
}
