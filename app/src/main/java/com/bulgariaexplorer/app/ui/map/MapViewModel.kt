package com.bulgariaexplorer.app.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.repository.PoiRepository
import kotlinx.coroutines.launch

class MapViewModel(private val poiRepository: PoiRepository) : ViewModel() {

    private val _poisState = MutableLiveData<PoiState>()
    val poisState: LiveData<PoiState> = _poisState

    init {
        loadPois()
    }

    private fun loadPois() {
        viewModelScope.launch {
            _poisState.value = PoiState.Loading
            val result = poiRepository.getAllPois()
            _poisState.value = if (result.isSuccess) {
                PoiState.Success(result.getOrNull()!!)
            } else {
                PoiState.Error(result.exceptionOrNull()?.message ?: "Failed to load POIs")
            }
        }
    }

    sealed class PoiState {
        object Loading : PoiState()
        data class Success(val pois: List<PoiResponse>) : PoiState()
        data class Error(val message: String) : PoiState()
    }
}
