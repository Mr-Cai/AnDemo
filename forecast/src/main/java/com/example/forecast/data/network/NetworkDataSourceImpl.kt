package com.example.forecast.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecast.data.unit.TAG
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Now
import com.example.forecast.internal.NoConnectivityException


class NetworkDataSourceImpl(
    private val weatherService: WeatherService
) : NetworkDataSource {
    private val _downloaderNowWeather = MutableLiveData<Now>()
    override val downloaderNowWeather: LiveData<Now> get() = _downloaderNowWeather
    override suspend fun getTodayWeatherAsync(cityName: String, langCode: String, unit: String) {
        try {
            val fetchTodayWeather = weatherService
                .getTodayWeatherAsync(cityName, langCode, unit).await()
            _downloaderNowWeather.postValue(fetchTodayWeather.weatherSet[0].now)
        } catch (e: NoConnectivityException) {
            Log.i(TAG, "fetchTodayWeather 网络连接状态:已断开", e)
        }
    }
}