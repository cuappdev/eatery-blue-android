package com.appdev.eateryblueandroid.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.appdev.eateryblueandroid.ui.appContext
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.codelab.android.datastore.PermissionSettings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LocationHandler {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var currentLocation: Location

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
            if (it != null) {
                currentLocation = it
            }
        }
    }

    fun getLocation(): Location? {
        return if (::currentLocation.isInitialized) currentLocation else null
    }

    fun locationPermissionRequested(value: Boolean){
        val permissionSettings = PermissionSettings.newBuilder()
            .setLocationAccess(value)
            .build()

        // Save to proto Datastore
        CoroutineScope(Dispatchers.IO).launch {
            appContext.userPreferencesStore.updateData { currentPreferences ->
                currentPreferences.toBuilder()
                    .setPermissionSettings(permissionSettings)
                    .build()
            }
        }
    }
}