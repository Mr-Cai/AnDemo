package com.example.forecast.data.unit

import android.content.Context
import android.widget.Toast

const val API_KEY = "604c3a417ef24a61ac201b467a7ce55c"
const val BASE_URL = "https://free-api.heweather.net/s6/weather/"
const val ICON = "https://cdn.heweather.com/cond_icon"
const val TODAY_WEATHER_ID = 0
const val WEATHER_BASIC_ID = 0
const val WEATHER_TIME_ID = 0
const val TAG = "调试日志"

// 常用小方法
fun toast(context: Context, msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()