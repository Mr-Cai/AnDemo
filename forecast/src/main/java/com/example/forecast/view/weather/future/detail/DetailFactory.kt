package com.example.forecast.view.weather.future.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.repository.WeatherRepository
import org.threeten.bp.LocalDate

class DetailFactory(
    private val startDate: LocalDate,
    private val repository: WeatherRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailViewModel(startDate, repository, unitProvider) as T
    }
}