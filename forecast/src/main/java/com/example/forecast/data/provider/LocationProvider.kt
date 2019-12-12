package com.example.forecast.data.provider

import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic

interface LocationProvider {
    suspend fun hasLocationChanged(location: Basic): Boolean
    suspend fun getPrefLocation(): String
}