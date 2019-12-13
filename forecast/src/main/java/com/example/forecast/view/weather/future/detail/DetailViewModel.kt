package com.example.forecast.view.weather.future.detail

import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.internal.lazyDeferred
import com.example.forecast.view.base.WeatherViewModel
import org.threeten.bp.LocalDate

class DetailViewModel(
    private val startDate: LocalDate,
    private val repository: WeatherRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(repository, unitProvider) {
    val weather by lazyDeferred {
        repository.fetchDetailWeather(startDate, super.isMetric)
    }
}
