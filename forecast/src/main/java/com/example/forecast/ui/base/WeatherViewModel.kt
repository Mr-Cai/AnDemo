package com.example.forecast.ui.base

import androidx.lifecycle.ViewModel
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.internal.UnitSystem
import com.example.forecast.internal.lazyDeferred

abstract class WeatherViewModel(
    private val repository: WeatherRepository,
    unitProvider: UnitProvider
) : ViewModel() {
    private val unitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean get() = unitSystem == UnitSystem.METRIC
    val weatherLocation by lazyDeferred {
        repository.getWeatherLocation()
    }
}