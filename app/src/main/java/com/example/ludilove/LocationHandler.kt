package com.example.ludilove

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource


class LocationHandler(private val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE_LOCATION_PERMISSION = 123
    var maxRequests : Int = 0;

    interface LocationCallback {
        fun onLocationReceived(latitude: Double, longitude: Double)
        fun onLocationFailed()
    }

    fun getCoordinates(callback: LocationCallback) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
            maxRequests++

            if(maxRequests < 3) {
                getCoordinates(callback)
                // Сюда нужно модальное окно для оповещения пользователя
            }

        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationTokenSource = CancellationTokenSource()
            val cancellationToken = cancellationTokenSource.token

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken
            ).addOnSuccessListener { location: android.location.Location? ->
                location?.let {
                    callback.onLocationReceived(location.latitude, location.longitude)
                } ?: run {
                    callback.onLocationFailed()
                }
            }.addOnFailureListener {
                callback.onLocationFailed()
            }
        }
    }


}

