package com.example.forecast.ui.weather.future.list

import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.internal.lazyDeferred
import com.example.forecast.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureViewModel(
    private val repository: WeatherRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(repository, unitProvider) {
    val future by lazyDeferred {
        repository.fetchFutureWeather(LocalDate.now(), super.isMetric)
    }
}
