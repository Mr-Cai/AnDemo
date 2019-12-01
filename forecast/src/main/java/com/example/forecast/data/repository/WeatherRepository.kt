package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.unit.UnitNowEntry

interface WeatherRepository {
    suspend fun fetchTodayWeather(metric: Boolean): LiveData<out UnitNowEntry>
}