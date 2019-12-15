package com.example.forecast.view.weather.future.list

import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.internal.lazyDeferred
import com.example.forecast.view.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureViewModel(
    private val repository: WeatherRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(repository, unitProvider) {
    val future by lazyDeferred {
        repository.fetchFuture(LocalDate.now(), super.isMetric)
    }

}
