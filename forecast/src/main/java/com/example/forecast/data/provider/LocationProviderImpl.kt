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
    private val appContext = context.applicationContext // 全局上下文
    private val useDeviceLoc = context.getString(R.string.use_device_location)  // 默认位置键(Key)
    private val customLoc = context.getString(R.string.custom_location)     // 自定义位置键(Key)

    override suspend fun hasLocationChanged(location: Basic): Boolean {
        val deviceLocation = try {
            hasDeviceChanged(location)
        } catch (e: NoLocPermission) {
            false
        }
        return deviceLocation || getCustomLoc() != location.cityName
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

    private suspend fun hasDeviceChanged(location: Basic): Boolean {
        if (!isUseDeviceLoc()) return false
        val deviceLocation = getLastDeviceLocAsync().await() ?: return false
        // 根据经纬度绝对值差值来判断位置是否改变，双精度小数不支持`==`比较
        return abs(deviceLocation.longitude - location.longitude.toDouble()) > 0.03 &&
                abs(deviceLocation.latitude - location.latitude.toDouble()) > 0.03

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

    private fun getCustomLoc() = preferences.getString(customLoc, null)
}