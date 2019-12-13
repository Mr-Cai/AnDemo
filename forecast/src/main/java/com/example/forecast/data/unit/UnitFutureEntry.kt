package com.example.forecast.data.unit

import org.threeten.bp.LocalDate

interface UnitFutureEntry {
    val forecastDate: LocalDate // 预报日期(2019-12-18)
    val tmpMax: String // 最高温度(26°C)
    val tmpMin: String // 最低温度(20°C)
    val condCodeDay: String // 白天天气状况代码(305: 小雨)
    val condTxTDay: String // 白天天气状况描述(小雨)
}