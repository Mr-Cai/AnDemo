package com.example.forecast.data.provider

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.forecast.R
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic
import com.example.forecast.internal.NoLocPermission
import com.example.forecast.internal.asDeferredAsync
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred
import kotlin.math.abs

class LocationProviderImpl(
    context: Context,
    private val locClient: FusedLocationProviderClient
) : PrefProvider(context), LocationProvider {
    private val appContext = context.applicationContext
    private val useDeviceLoc = context.getString(R.string.use_device_location)
    private val customLoc = context.getString(R.string.custom_location)


    override suspend fun hasLocationChanged(location: Basic): Boolean {
        val deviceLocation = try {
            hasDeviceChanged(location)
        } catch (e: NoLocPermission) {
            false
        }
        return deviceLocation || hasCustomChanged(location)
    }

    override suspend fun getPrefLocation(): String {
        return when {
            isUseDeviceLoc() -> try {
                val deviceLoc = getLastDeviceLocAsync().await() ?: return "${getCustomLoc()}"
                "${deviceLoc.longitude},${deviceLoc.latitude}"
            } catch (e: NoLocPermission) {
                "${getCustomLoc()}"
            }
            else -> "${getCustomLoc()}"
        }
    }

    private suspend fun hasDeviceChanged(location: Basic?): Boolean {
        if (!isUseDeviceLoc()) return false
        val deviceLocation = getLastDeviceLocAsync().await() ?: return false
        return abs(deviceLocation.latitude - location!!.latitude.toDouble()) > 0.03 &&
                abs(deviceLocation.longitude - location.longitude.toDouble()) > 0.03
    }


    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocAsync(): Deferred<Location?> {
        return when (PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                appContext,
                ACCESS_COARSE_LOCATION
            ) -> locClient.lastLocation.asDeferredAsync()
            else -> throw NoLocPermission()
        }
    }

    private fun isUseDeviceLoc() = preferences.getBoolean(useDeviceLoc, true)

    private fun hasCustomChanged(location: Basic): Boolean {
        val customLoc = getCustomLoc()
        return customLoc != location.cityName
    }


    private fun getCustomLoc() = preferences.getString(customLoc, null)
}