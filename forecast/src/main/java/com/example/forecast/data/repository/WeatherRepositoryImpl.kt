package com.example.forecast.data.repository

import com.example.forecast.data.NowWeatherDAO
import com.example.forecast.data.WeatherResponse.WeatherSet.Now
import com.example.forecast.data.network.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class WeatherRepositoryImpl(
    private val weatherDAO: NowWeatherDAO,
    private val networkDataSource: NetworkDataSource
) : WeatherRepository {
    init {
        networkDataSource.downloaderNowWeather.observeForever { newWeather ->
            persistFetchedTodayWeather(newWeather)
        }
    }

    override suspend fun fetchTodayWeather(metric: Boolean) = withContext(Dispatchers.IO) {
        initWeatherData()
        return@withContext if (metric) weatherDAO.getWeatherMetric()
        else weatherDAO.getWeatherImperial()
    }

    private suspend fun initWeatherData() {
        if (isFetchedDataTime(ZonedDateTime.now().minusDays(1)))
            fetchNow()
    }

    private suspend fun fetchNow() {
        networkDataSource.getTodayWeatherAsync("深圳", Locale.getDefault().language)
    }

    private fun isFetchedDataTime(lastFetchTime: ZonedDateTime): Boolean {
        val time30MinAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(time30MinAgo)
    }

    private fun persistFetchedTodayWeather(response: Now) {
        GlobalScope.launch(Dispatchers.IO) {
            weatherDAO.upsert(response)
        }
    }


}