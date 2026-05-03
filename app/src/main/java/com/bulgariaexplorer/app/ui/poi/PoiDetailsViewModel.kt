package com.bulgariaexplorer.app.ui.poi

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulgariaexplorer.app.data.remote.dto.CreateVisitRequest
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.remote.dto.VisitResponse
import com.bulgariaexplorer.app.data.repository.FavoriteRepository
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.VisitRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PoiDetailsViewModel(
    private val poiRepository: PoiRepository,
    private val favoriteRepository: FavoriteRepository,
    private val visitRepository: VisitRepository
) : ViewModel() {

    private val _poiState = MutableLiveData<PoiState>()
    val poiState: LiveData<PoiState> = _poiState

    private val _favoriteState = MutableLiveData<FavoriteState>()
    val favoriteState: LiveData<FavoriteState> = _favoriteState

    private val _visitState = MutableLiveData<VisitState>()
    val visitState: LiveData<VisitState> = _visitState

    private var currentPoiId: Long? = null
    private var currentPoi: PoiResponse? = null
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    fun loadPoi(poiId: Long) {
        currentPoiId = poiId
        viewModelScope.launch {
            _poiState.value = PoiState.Loading
            val result = poiRepository.getPoiById(poiId)
            _poiState.value = if (result.isSuccess) {
                currentPoi = result.getOrNull()
                PoiState.Success(result.getOrNull()!!)
            } else {
                PoiState.Error(result.exceptionOrNull()?.message ?: "Failed to load POI")
            }
        }
    }

    fun updateCurrentLocation(fusedLocationClient: FusedLocationProviderClient) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLat = it.latitude
                    currentLng = it.longitude
                }
            }
        } catch (e: SecurityException) {
            // Handle permission denied
        }
    }

    fun checkInWithPhoto(photoUri: Uri) {
        val poi = currentPoi ?: return
        val lat = currentLat ?: return
        val lng = currentLng ?: return

        viewModelScope.launch {
            _visitState.value = VisitState.Loading

            // Upload photo first
            val uploadResult = visitRepository.uploadImage(photoUri)
            if (uploadResult.isFailure) {
                _visitState.value = VisitState.Error(uploadResult.exceptionOrNull()?.message ?: "Failed to upload photo")
                return@launch
            }

            val photoUrl = uploadResult.getOrNull()!!.url

            // Calculate distance
            val distance = calculateDistance(lat, lng, poi.latitude, poi.longitude)

            // Create visit
            val visitRequest = CreateVisitRequest(
                poiId = poi.id,
                latitude = BigDecimal.valueOf(lat),
                longitude = BigDecimal.valueOf(lng),
                distanceMeters = BigDecimal.valueOf(distance),
                photoUrl = photoUrl
            )

            val visitResult = visitRepository.createVisit(visitRequest)
            _visitState.value = if (visitResult.isSuccess) {
                VisitState.Success(visitResult.getOrNull()!!)
            } else {
                VisitState.Error(visitResult.exceptionOrNull()?.message ?: "Failed to check in")
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun toggleFavorite() {
        val poiId = currentPoiId ?: return
        val currentState = _favoriteState.value

        viewModelScope.launch {
            _favoriteState.value = FavoriteState.Loading

            val result = if (currentState is FavoriteState.IsFavorite && currentState.isFavorite) {
                favoriteRepository.removeFavorite(poiId)
            } else {
                favoriteRepository.addFavorite(poiId)
            }

            _favoriteState.value = if (result.isSuccess) {
                val isFavorite = currentState !is FavoriteState.IsFavorite || !currentState.isFavorite
                FavoriteState.IsFavorite(isFavorite)
            } else {
                FavoriteState.Error(result.exceptionOrNull()?.message ?: "Operation failed")
            }
        }
    }

    fun setIsFavorite(isFavorite: Boolean) {
        _favoriteState.value = FavoriteState.IsFavorite(isFavorite)
    }

    sealed class PoiState {
        object Loading : PoiState()
        data class Success(val poi: PoiResponse) : PoiState()
        data class Error(val message: String) : PoiState()
    }

    sealed class FavoriteState {
        object Loading : FavoriteState()
        data class IsFavorite(val isFavorite: Boolean) : FavoriteState()
        data class Error(val message: String) : FavoriteState()
    }

    sealed class VisitState {
        object Idle : VisitState()
        object Loading : VisitState()
        data class Success(val visit: VisitResponse) : VisitState()
        data class Error(val message: String) : VisitState()
    }
}
