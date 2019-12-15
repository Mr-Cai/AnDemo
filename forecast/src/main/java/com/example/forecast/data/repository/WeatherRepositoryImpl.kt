package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.database.FutureDAO
import com.example.forecast.data.database.LocationDAO
import com.example.forecast.data.database.NowWeatherDAO
import com.example.forecast.data.database.TimeDAO
import com.example.forecast.data.network.NetworkDataSource
import com.example.forecast.data.network.WeatherResponse.WeatherSet.*
import com.example.forecast.data.provider.LocationProvider
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.unit.UnitDetailEntry
import com.example.forecast.data.unit.UnitFutureEntry
import com.example.forecast.data.unit.UnitNowEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import java.util.*

class WeatherRepositoryImpl(
    private val weatherDAO: NowWeatherDAO,
    private val locationDAO: LocationDAO,
    private val timeDAO: TimeDAO,
    private val futureDAO: FutureDAO,
    private val networkDataSource: NetworkDataSource,
    private val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider
) : WeatherRepository {
    init {
        networkDataSource.apply {
            downloaderNowWeather.observeForever { persistFetchCurrent(it) }
            downloaderBasic.observeForever { persistFetchCurrent(it) }
            downloaderForecast.observeForever { persistFetchFuture(it) }
        }
    }

    override suspend fun fetchNow(metric: Boolean): LiveData<out UnitNowEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) weatherDAO.fetchNowWeather()
            else weatherDAO.fetchNowWeather()
        }
    }

    override suspend fun fetchFuture(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitFutureEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) futureDAO.queryFutureMetric(startDate)
            else futureDAO.queryFutureImperial(startDate)
        }
    }

    override suspend fun fetchDetail(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out UnitDetailEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) futureDAO.queryDetail(startDate)
            else futureDAO.queryDetail(startDate)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<Basic> {
        return withContext(Dispatchers.IO) {
            return@withContext locationDAO.queryLocation()
        }
    }

    override suspend fun getTimeZone(): LiveData<Update> {
        return withContext(Dispatchers.IO) {
            return@withContext timeDAO.getTimeZone()
        }
    }

    private suspend fun initWeatherData() {
        val lastLocation = locationDAO.queryLocationNonLive()
        val lastTimeZone = timeDAO.getTimeZone().value
        when {
            lastTimeZone == null || locationProvider.hasLocationChanged(lastLocation!!) -> {
                fetchAllWeather()
                return
            }
            isFetchedDataTime(lastTimeZone.zonedDateTime) -> fetchAllWeather()
            isFetchedFutureNeeded() -> fetchAllWeather()
        }
    }

    private fun isFetchedFutureNeeded(): Boolean {
        val futureCount = futureDAO.countFuture(LocalDate.now())
        return futureCount < 7
    }


    private suspend fun fetchAllWeather() {
        val unit = unitProvider.getUnitSystem().name.substring(0, 1)
            .toLowerCase(Locale.getDefault())
        networkDataSource.getWeatherResponse(
            when {
                locationProvider.getPrefLocation().isEmpty() -> "北京"
                else -> locationProvider.getPrefLocation()
            },
            Locale.getDefault().language,
            unit
        )
    }


    private fun isFetchedDataTime(lastFetchTime: ZonedDateTime): Boolean {
        val time30MinAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(time30MinAgo)
    }

    private fun persistFetchCurrent(response: Now) {
        GlobalScope.launch(Dispatchers.IO) {
            weatherDAO.insertData(response)
        }
    }

    private fun persistFetchCurrent(response: Basic) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDAO.insertData(response)
        }
    }


    private fun persistFetchFuture(response: List<DailyForecast>) {
        GlobalScope.launch(Dispatchers.IO) {
            futureDAO.deleteFuture(LocalDate.now())
            futureDAO.insertData(response)
        }
    }
}