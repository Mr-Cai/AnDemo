package com.example.forecast.data.unit

import org.threeten.bp.LocalDate

interface UnitDetailEntry {
    val forecastDate: LocalDate // 预报日期(2019-12-18)
    val humidity: String // 相对湿度(83)
    val moonRise: String // 月升时间(23:49)
    val moonSet: String // 月落时间(11:58)
    val precipitation: String // 降水量(0.0)
    val pop: String // 降水概率(Probability of Precipitation: 7)
    val pressure: String // 大气压强(1008)
    val sunRise: String // 日出时间(06:58)
    val sunSet: String // 日落时间(17:43)
    val tmpMax: String // 最高温度(26°C)
    val tmpMin: String // 最低温度(20°C)
    val uvIndex: String // 紫外线强度指数(ultraviolet ray: 4)
    val condCodeDay: String // 白天天气状况代码(305: 小雨)
    val condCodeNight: String // 夜间天气状况代码(101: 多云)
    val condTxTDay: String // 白天天气状况描述(小雨)
    val condTxTNight: String // 晚间天气状况描述(小雨)
    val visibility: String // 能见度(24)
    val windDeg: String // 风向角度(46°)
    val windDir: String // 风向(东北风)
    val windPower: String // 风力(3)
    val windSpeed: String // 风速
}