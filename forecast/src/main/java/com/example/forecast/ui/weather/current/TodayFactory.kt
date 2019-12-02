package com.example.forecast.ui.weather.current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository

class TodayFactory(
    private val repository: WeatherRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TodayViewModel(repository, unitProvider) as T
    }
}