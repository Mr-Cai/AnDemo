package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.database.LocationDAO
import com.example.forecast.data.database.NowWeatherDAO
import com.example.forecast.data.database.TimeDAO
import com.example.forecast.data.network.NetworkDataSource
import com.example.forecast.data.network.WeatherResponse.WeatherSet.*
import com.example.forecast.data.provider.LocationProvider
import com.example.forecast.data.provider.UnitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class WeatherRepositoryImpl(
    private val weatherDAO: NowWeatherDAO,
    private val locationDAO: LocationDAO,
    private val timeDAO: TimeDAO,
    private val networkDataSource: NetworkDataSource,
    private val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider
) : WeatherRepository {
    init {
        networkDataSource.downloaderNowWeather.observeForever { newWeather ->
            persistFetchedTodayWeather(newWeather)
        }
    }

    override suspend fun fetchTodayWeather(metric: Boolean) = withContext(Dispatchers.IO) {
        initWeatherData()
        return@withContext if (metric) weatherDAO.fetchNowWeather()
        else weatherDAO.fetchNowWeather()
    }

    override suspend fun getWeatherLocation(): LiveData<Basic> {
        return withContext(Dispatchers.IO) {
            return@withContext locationDAO.getLocation()
        }
    }

    override suspend fun getTimeZone(): LiveData<Update> {
        return withContext(Dispatchers.IO) {
            return@withContext timeDAO.getTimeZone()
        }
    }

    private suspend fun initWeatherData() {
        val lastLocation = locationDAO.getLocation().value
        val lastTimeZone = timeDAO.getTimeZone().value
        when {
            lastTimeZone == null || locationProvider.hasLocationChanged(lastLocation) -> {
                fetchNow()
                return
            }
            isFetchedDataTime(lastTimeZone.zonedDateTime) -> fetchNow()
        }
    }

    private suspend fun fetchNow() {
        val unit = unitProvider.getUnitSystem().name.substring(0, 1)
            .toLowerCase(Locale.getDefault())
        networkDataSource.getTodayWeatherAsync(
            locationProvider.getPrefLocation(),
            Locale.getDefault().language,
            unit
        )
    }

    private fun isFetchedDataTime(lastFetchTime: ZonedDateTime): Boolean {
        val time30MinAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(time30MinAgo)
    }

    private fun persistFetchedTodayWeather(response: Now) {
        GlobalScope.launch(Dispatchers.IO) {
            weatherDAO.insertData(response)
        }
    }

    private fun persistFetchedTodayWeather(response: Basic) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDAO.insertData(response)
        }
    }

    private fun persistFetchedTodayWeather(response: Update) {
        GlobalScope.launch(Dispatchers.IO) {
            timeDAO.insertData(response)
        }
    }
}