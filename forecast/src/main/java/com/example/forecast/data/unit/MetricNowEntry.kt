package com.example.forecast.data.unit

import androidx.room.ColumnInfo

// 公制单位
data class MetricNowEntry(
    @ColumnInfo(name = "feelTemp") override val feelTemp: String, // 体感温度
    @ColumnInfo(name = "temperature") override val temperature: String, // 温度°C 🌡 ️
    @ColumnInfo(name = "cloudCover") override val cloudCover: String, // 云量 ☁️
    @ColumnInfo(name = "conditionCode") override val conditionCode: String, // 实况天气代码 (100:☀️)
    @ColumnInfo(name = "conditionDesc") override val conditionDesc: String, // 实况天气描述
    @ColumnInfo(name = "humidity") override val humidity: String, // 相对湿度
    @ColumnInfo(name = "precipitation") override val precipitation: String, // 降水量
    @ColumnInfo(name = "pressure") override val pressure: String, // 大气压强
    @ColumnInfo(name = "visibility") override val visibility: String, // 能见度
    @ColumnInfo(name = "windDeg") override val windDeg: String, // 风向角度(46°)
    @ColumnInfo(name = "windDir") override val windDir: String, // 风向(东北风)
    @ColumnInfo(name = "windPower") override val windPower: String, // 风力(3)
    @ColumnInfo(name = "windSpeed") override val windSpeed: String // 风速
) : UnitNowEntry