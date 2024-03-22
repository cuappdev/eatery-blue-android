package com.cornellappdev.android.eateryblue.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationHandler {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var currentLocation = mutableStateOf<Location?>(null)

    fun instantiate(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        updateLocation(context)
    }

    private fun updateLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            currentLocation.value = it
        }
    }
}
