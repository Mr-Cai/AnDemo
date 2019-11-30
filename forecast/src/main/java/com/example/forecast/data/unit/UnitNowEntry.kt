package com.example.forecast.data.unit

interface UnitNowEntry {
    val feelTemp: String // 体感温度
    val temperature: String // 温度°C 🌡 ️
    val cloudCover: String // 云量 ☁️
    val conditionCode: String // 实况天气代码 (100:☀️)
    val conditionDesc: String // 实况天气描述
    val humidity: String // 相对湿度
    val precipitation: String // 降水量
    val pressure: String // 大气压强
    val visibility: String // 能见度
    val windDeg: String // 风向角度(46°)
    val windDir: String // 风向(东北风)
    val windPower: String // 风力(3)
    val windSpeed: String // 风速
}