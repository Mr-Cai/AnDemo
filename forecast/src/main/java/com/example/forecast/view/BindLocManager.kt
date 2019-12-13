package com.example.forecast.view

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

class BindLocManager(
    lifecycleOwner: LifecycleOwner,
    private val locClient: FusedLocationProviderClient,
    private val locationCallback: LocationCallback
) : LifecycleObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val locationRequest = LocationRequest().apply {
        interval = 5000L
        fastestInterval = 5000L
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @SuppressLint("MissingPermission")
    fun startLocUpdates() {
        locClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @SuppressLint("MissingPermission")
    fun removeLocUpdates() {
        locClient.removeLocationUpdates(locationCallback)
    }
}