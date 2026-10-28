package com.example.geotracker.data.model

import com.google.gson.annotations.SerializedName

data class LocationRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

data class LocationItem(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("created_at")
    val createdAt: String? = null
)
