package com.example.forecast.data.network

import androidx.lifecycle.LiveData
import com.example.forecast.data.network.WeatherResponse.WeatherSet.*

interface NetworkDataSource {
    val downloaderNowWeather: LiveData<Now>
    val downloaderBasic: LiveData<Basic>
    val downloaderForecast: LiveData<List<DailyForecast>>

    suspend fun getWeatherResponse(
        cityName: String, // 查询城市
        langCode: String = "zh", // 语言(默认中文)
        unit: String = "m" // 单位(m:公制(默认) i:英制)
    )
}