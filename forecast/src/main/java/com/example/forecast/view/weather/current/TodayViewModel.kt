package com.example.forecast.view.weather.current

import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.internal.lazyDeferred
import com.example.forecast.view.base.WeatherViewModel

class TodayViewModel(
    private val repository: WeatherRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(repository, unitProvider) {
    val weather by lazyDeferred {
        repository.fetchTodayWeather(super.isMetric)
    }
}
