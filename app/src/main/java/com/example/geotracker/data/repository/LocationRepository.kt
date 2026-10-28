package com.example.geotracker.data.repository

import android.content.Context
import com.example.geotracker.data.model.LocationItem
import com.example.geotracker.data.model.LocationRequest
import com.example.geotracker.data.remote.RetrofitClient

class LocationRepository(context: Context) {
    private val apiService = RetrofitClient.getApiService(context)

    suspend fun saveLocation(
        name: String,
        description: String,
        latitude: Double,
        longitude: Double
    ): Result<LocationItem> {
        return try {
            val request = LocationRequest(
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude
            )
            val response = apiService.saveLocation(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = response.errorBody()?.string() ?: "Error al guardar ubicación: ${response.code()}"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al guardar: ${e.localizedMessage ?: "Desconocido"}"))
        }
    }

    suspend fun getLocations(): Result<List<LocationItem>> {
        return try {
            val response = apiService.getLocations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = response.errorBody()?.string() ?: "Error al consultar ubicaciones: ${response.code()}"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al consultar: ${e.localizedMessage ?: "Desconocido"}"))
        }
    }
}
