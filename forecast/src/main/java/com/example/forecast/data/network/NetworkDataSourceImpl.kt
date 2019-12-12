package com.example.forecast.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Now
import com.example.forecast.data.unit.TAG
import com.example.forecast.internal.NoConnectivityException


class NetworkDataSourceImpl(
    private val weatherService: WeatherService
) : NetworkDataSource {
    private val _downloaderNowWeather = MutableLiveData<Now>()
    private val _downloaderBasic = MutableLiveData<Basic>()

    override val downloaderNowWeather: LiveData<Now> get() = _downloaderNowWeather
    override val downloaderBasic: LiveData<Basic> get() = _downloaderBasic

    override suspend fun getTodayWeatherAsync(cityName: String, langCode: String, unit: String) {
        try {
            val fetchTodayWeather = weatherService
                .getTodayWeatherAsync(cityName, langCode, unit).await()
            _downloaderNowWeather.postValue(fetchTodayWeather.weatherSet[0].now)
            _downloaderBasic.postValue(fetchTodayWeather.weatherSet[0].basic)
        } catch (e: NoConnectivityException) {
            Log.i(TAG, "fetchTodayWeather 网络连接状态:已断开", e)
        }
    }
}