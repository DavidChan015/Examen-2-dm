package com.example.geotracker.ui.screens.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.model.LocationItem
import com.example.geotracker.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationListUiState(
    val locations: List<LocationItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LocationListViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository(application)

    private val _uiState = MutableStateFlow(LocationListUiState())
    val uiState: StateFlow<LocationListUiState> = _uiState.asStateFlow()

    init {
        fetchLocations()
    }

    fun fetchLocations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = locationRepository.getLocations()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locations = result.getOrDefault(emptyList())
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar la lista de ubicaciones"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }
}
