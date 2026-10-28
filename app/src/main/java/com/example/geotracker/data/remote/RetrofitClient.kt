package com.example.geotracker.data.remote

import android.content.Context
import com.example.geotracker.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Default URL pointing to localhost of host machine from Android Emulator
    var BASE_URL = "http://44.195.39.128:8001/api/"

    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: createApiService(context).also { apiService = it }
        }
    }

    private fun createApiService(context: Context): ApiService {
        val tokenManager = TokenManager.getInstance(context)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
