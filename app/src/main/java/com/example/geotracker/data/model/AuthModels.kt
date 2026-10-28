package com.example.geotracker.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("user")
    val user: User? = null
) {
    fun getEffectiveToken(): String? = token ?: accessToken
}

data class User(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("username")
    val username: String? = null
)
