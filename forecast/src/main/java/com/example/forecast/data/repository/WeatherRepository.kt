package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Update
import com.example.forecast.data.unit.UnitDetailEntry
import com.example.forecast.data.unit.UnitFutureEntry
import com.example.forecast.data.unit.UnitNowEntry
import org.threeten.bp.LocalDate

interface WeatherRepository {
    suspend fun fetchNow(metric: Boolean): LiveData<out UnitNowEntry>
    suspend fun fetchFuture(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitFutureEntry>>

    suspend fun fetchDetail(startDate: LocalDate, metric: Boolean): LiveData<out UnitDetailEntry>
    suspend fun getWeatherLocation(): LiveData<Basic>
    suspend fun getTimeZone(): LiveData<Update>
}