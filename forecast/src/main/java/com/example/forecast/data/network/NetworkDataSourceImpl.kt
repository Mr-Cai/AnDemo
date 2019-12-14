package com.example.forecast.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecast.data.network.WeatherResponse.WeatherSet.*
import com.example.forecast.data.unit.TAG
import com.example.forecast.internal.NoConnectivity

class NetworkDataSourceImpl(
    private val weatherService: WeatherService
) : NetworkDataSource {
    private val _downloaderNowWeather = MutableLiveData<Now>()
    override val downloaderNowWeather: LiveData<Now> get() = _downloaderNowWeather

    private val _downloaderBasic = MutableLiveData<Basic>()
    override val downloaderBasic: LiveData<Basic> get() = _downloaderBasic

    private val _downloaderForecast = MutableLiveData<List<DailyForecast>>()
    override val downloaderForecast: LiveData<List<DailyForecast>> get() = _downloaderForecast

    override suspend fun getWeatherResponse(cityName: String, langCode: String, unit: String) {
        try {
            val current = weatherService.getCurrentAsync(cityName, langCode, unit).await()
            _downloaderNowWeather.postValue(current.weatherSet[0].now)
            _downloaderBasic.postValue(current.weatherSet[0].basic)

            val forecast = weatherService.getForecastAsync(cityName, langCode, unit).await()
            _downloaderForecast.postValue(forecast.weatherSet[0].forecast)
        } catch (e: NoConnectivity) {
            Log.i(TAG, "getWeatherResponse 网络连接状态:已断开", e)
        }
    }
}