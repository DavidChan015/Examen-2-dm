package com.example.geotracker.data.remote

import com.example.geotracker.data.model.LocationItem
import com.example.geotracker.data.model.LocationRequest
import com.example.geotracker.data.model.LoginRequest
import com.example.geotracker.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("login")
    suspend fun loginAlternative(@Body request: LoginRequest): Response<LoginResponse>

    @POST("locations")
    suspend fun saveLocation(@Body request: LocationRequest): Response<LocationItem>

    @GET("locations")
    suspend fun getLocations(): Response<List<LocationItem>>
}
