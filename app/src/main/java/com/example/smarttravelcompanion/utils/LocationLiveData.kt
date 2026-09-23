package com.example.smarttravelcompanion.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.smarttravelcompanion.data.LocationDetails
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationReceived: (LocationDetails) -> Unit) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            location?.let {
                onLocationReceived(LocationDetails(it.latitude, it.longitude))
            }
        }
    }
}