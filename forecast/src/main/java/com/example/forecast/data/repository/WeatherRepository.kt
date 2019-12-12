package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Update
import com.example.forecast.data.unit.UnitNowEntry

interface WeatherRepository {
    suspend fun fetchTodayWeather(metric: Boolean): LiveData<out UnitNowEntry>
    suspend fun getWeatherLocation(): LiveData<Basic>
    suspend fun getTimeZone(): LiveData<Update>
}