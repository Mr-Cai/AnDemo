package com.example.forecast.data.network

import androidx.lifecycle.LiveData
import com.example.forecast.data.WeatherResponse.WeatherSet.Now

interface NetworkDataSource {
    val downloaderNowWeather: LiveData<Now>
    suspend fun getTodayWeatherAsync(
        cityName: String, // 查询城市
        langCode: String = "zh", // 语言(默认中文)
        unit: String = "m" // 单位(m:公制(默认) i:英制)
    )
}