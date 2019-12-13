package com.example.forecast.view.weather.future.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository

class FutureFactory(
    private val repository: WeatherRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FutureViewModel(repository, unitProvider) as T
    }
}