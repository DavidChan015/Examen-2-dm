package com.example.geotracker.data.remote

import com.example.geotracker.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        return if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        } else {
            val newRequest = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        }
    }
}
