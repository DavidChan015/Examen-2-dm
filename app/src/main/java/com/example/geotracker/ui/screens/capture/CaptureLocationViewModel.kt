package com.example.geotracker.ui.screens.capture

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.repository.LocationRepository
import com.example.geotracker.location.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CaptureUiState(
    val name: String = "",
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class CaptureLocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository(application)
    private val locationHelper = LocationHelper(application)

    private val _uiState = MutableStateFlow(CaptureUiState())
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null, successMessage = null)
    }

    fun onDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = null, successMessage = null)
    }

    fun captureAndSaveLocation() {
        val current = _uiState.value
        if (current.name.isBlank() || current.description.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Ingresa el nombre y la descripción.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            // Step 1: Obtain GPS Location
            val location = locationHelper.getCurrentLocation()
            if (location == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No se pudo obtener la ubicación GPS actual. Asegúrate de tener el GPS activado."
                )
                return@launch
            }

            val lat = location.latitude
            val lng = location.longitude

            _uiState.value = _uiState.value.copy(latitude = lat, longitude = lng)

            // Step 2: Save location to backend
            val result = locationRepository.saveLocation(
                name = current.name.trim(),
                description = current.description.trim(),
                latitude = lat,
                longitude = lng
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Ubicación guardada exitosamente en el servidor (Lat: %.4f, Lng: %.4f)".format(lat, lng),
                    name = "",
                    description = "",
                    latitude = null,
                    longitude = null
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al guardar ubicación"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
