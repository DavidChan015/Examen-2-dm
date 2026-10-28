package com.example.geotracker.data.repository

import android.content.Context
import com.example.geotracker.data.local.TokenManager
import com.example.geotracker.data.model.LoginRequest
import com.example.geotracker.data.model.LoginResponse
import com.example.geotracker.data.remote.RetrofitClient

class AuthRepository(context: Context) {
    private val apiService = RetrofitClient.getApiService(context)
    private val tokenManager = TokenManager.getInstance(context)

    suspend fun login(userOrEmail: String, pass: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(
                username = userOrEmail,
                email = userOrEmail,
                password = pass
            )

            val response = try {
                apiService.login(request)
            } catch (e: Exception) {
                // Fallback to /api/login if /api/auth/login fails or 404
                apiService.loginAlternative(request)
            }

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.getEffectiveToken()
                if (!token.isNullOrEmpty()) {
                    tokenManager.saveToken(token)
                }
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error de autenticación: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage ?: "Servidor no disponible"}"))
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.hasToken()
    }
}
