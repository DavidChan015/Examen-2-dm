package com.example.geotracker.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                if (continuation.isActive) continuation.resume(location)
            } else {
                // Fallback to last known location if getCurrentLocation returns null
                fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                    if (continuation.isActive) continuation.resume(lastLoc)
                }.addOnFailureListener {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        }.addOnFailureListener {
            if (continuation.isActive) continuation.resume(null)
        }

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }
}
