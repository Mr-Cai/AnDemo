package com.example.forecast.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.internal.UnitSystem
import com.example.forecast.internal.lazyDeferred

class TodayViewModel(
    private val repository: WeatherRepository,
    unitProvider: UnitProvider
) : ViewModel() {
    private val unitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean get() = unitSystem == UnitSystem.METRIC
    val weather by lazyDeferred {
        repository.fetchTodayWeather(isMetric)
    }
    val weatherLocation by lazyDeferred {
        repository.getWeatherLocation()
    }
}
