package com.example.forecast.data.provider

import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic

class LocationProviderImpl : LocationProvider {
    override suspend fun hasLocationChanged(location: Basic?) = true
    override suspend fun getPrefLocation() = "深圳"
}