package com.bulgariaexplorer.app.ui.poi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.utils.TransliterationUtils
import kotlinx.coroutines.launch

class PoiViewModel(private val poiRepository: PoiRepository) : ViewModel() {

    private val _poisState = MutableLiveData<PoiState>()
    val poisState: LiveData<PoiState> = _poisState

    private var allPois: List<PoiResponse> = emptyList()

    init {
        loadPois()
    }

    fun loadPois() {
        viewModelScope.launch {
            _poisState.value = PoiState.Loading
            val result = poiRepository.getAllPois()
            _poisState.value = if (result.isSuccess) {
                allPois = result.getOrNull()!!
                PoiState.Success(allPois)
            } else {
                PoiState.Error(result.exceptionOrNull()?.message ?: "Failed to load POIs")
            }
        }
    }

    fun searchPois(query: String) {
        if (query.isEmpty()) {
            _poisState.value = PoiState.Success(allPois)
            return
        }

        val filtered = allPois.filter {
            TransliterationUtils.matchesSearch(it.title, query) ||
            TransliterationUtils.matchesSearch(it.shortDescription, query) ||
            TransliterationUtils.matchesSearch(it.city, query) ||
            TransliterationUtils.matchesSearch(it.address, query)
        }
        _poisState.value = PoiState.Success(filtered)
    }

    sealed class PoiState {
        object Loading : PoiState()
        data class Success(val pois: List<PoiResponse>) : PoiState()
        data class Error(val message: String) : PoiState()
    }
}
