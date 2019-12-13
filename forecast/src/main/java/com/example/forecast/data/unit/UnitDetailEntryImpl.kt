package com.example.forecast.data.unit

import androidx.room.ColumnInfo
import org.threeten.bp.LocalDate

data class UnitDetailEntryImpl(
    @ColumnInfo(name = "forecastDate") override val forecastDate: LocalDate, // 预报日期(2019-12-18)
    @ColumnInfo(name = "humidity") override val humidity: String, // 相对湿度(83)
    @ColumnInfo(name = "moonRise") override val moonRise: String, // 月升时间(23:49)
    @ColumnInfo(name = "moonSet") override val moonSet: String, // 月落时间(11:58)
    @ColumnInfo(name = "precipitation") override val precipitation: String, // 降水量(0.0)
    @ColumnInfo(name = "pop") override val pop: String, // 降水概率(Probability of Precipitation: 7)
    @ColumnInfo(name = "pressure") override val pressure: String, // 大气压强(1008)
    @ColumnInfo(name = "sunRise") override val sunRise: String, // 日出时间(06:58)
    @ColumnInfo(name = "sunSet") override val sunSet: String, // 日落时间(17:43)
    @ColumnInfo(name = "tmpMax") override val tmpMax: String, // 最高温度(26°C)
    @ColumnInfo(name = "tmpMin") override val tmpMin: String, // 最低温度(20°C)
    @ColumnInfo(name = "uvIndex") override val uvIndex: String, // 紫外线强度指数(ultraviolet ray: 4)
    @ColumnInfo(name = "condTxTDay") override val condTxTDay: String, // 白天天气状况描述(小雨)
    @ColumnInfo(name = "condTxTNight") override val condTxTNight: String, // 晚间天气状况描述(小雨)
    @ColumnInfo(name = "visibility") override val visibility: String, // 能见度(24)
    @ColumnInfo(name = "windDeg") override val windDeg: String, // 风向角度(46°)
    @ColumnInfo(name = "windDir") override val windDir: String, // 风向(东北风)
    @ColumnInfo(name = "windPower") override val windPower: String, // 风力(3)
    @ColumnInfo(name = "windSpeed") override val windSpeed: String // 风速
) : UnitDetailEntry