package com.example.geotracker.ui.screens.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.model.LocationItem
import com.example.geotracker.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val locations: List<LocationItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedLocation: LocationItem? = null
)

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository(application)

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = locationRepository.getLocations()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locations = result.getOrDefault(emptyList())
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar ubicaciones para el mapa"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    fun selectLocation(location: LocationItem) {
        _uiState.value = _uiState.value.copy(selectedLocation = location)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(selectedLocation = null)
    }
}
