package com.example.forecast.data.network

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.forecast.data.unit.TODAY_WEATHER_ID
import com.example.forecast.data.unit.WEATHER_BASIC_ID
import com.example.forecast.data.unit.WEATHER_TIME_ID
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.text.SimpleDateFormat
import java.util.*

// 开发文档: https://dev.heweather.com/docs/api/weather
@Suppress("SpellCheckingInspection")
@Parcelize
data class WeatherResponse(
    @SerializedName("HeWeather6") val weatherSet: List<WeatherSet>
) : Parcelable {
    @Parcelize
    data class WeatherSet(
        val basic: Basic,  // 基础信息
        @Embedded(prefix = "condition_") val now: Now,   // 实况天气
        val status: String, // 接口状态(ok)
        val update: Update  // 接口更新时间
    ) : Parcelable {
        @Parcelize
        @Entity(tableName = "weather_basic")
        data class Basic(
            @SerializedName("cid") val cityID: String, // 城市编号
            @SerializedName("location") val cityName: String, // 城市名称
            @SerializedName("lon") val longitude: String, // 城市经度
            @SerializedName("lat") val latitude: String, // 城市纬度
            @SerializedName("parent_city") val parentCity: String, // 上级城市
            @SerializedName("admin_area") val adminArea: String, // 行政区域
            @SerializedName("cnty") val region: String, // 所属国家
            @SerializedName("tz") val timeZone: String // 所在时区
        ) : Parcelable {
            @IgnoredOnParcel
            @PrimaryKey(autoGenerate = false)
            var id: Int = WEATHER_BASIC_ID
        }

        @Parcelize
        @Entity(tableName = "today_weather")
        data class Now(
            @SerializedName("fl") val feelTemp: String, // 体感温度
            @SerializedName("tmp") val temperature: String, // 温度°C 🌡 ️
            @SerializedName("cloud") val cloudCover: String, // 云量 ☁️
            @SerializedName("cond_code") val conditionCode: String, // 实况天气代码 (100:☀️)
            @SerializedName("cond_txt") val conditionDesc: String, // 实况天气描述
            @SerializedName("hum") val humidity: String, // 相对湿度
            @SerializedName("pcpn") val precipitation: String, // 降水量
            @SerializedName("pres") val pressure: String, // 大气压强
            @SerializedName("vis") val visibility: String, // 能见度
            @SerializedName("wind_deg") val windDeg: String, // 风向角度(46°)
            @SerializedName("wind_dir") val windDir: String, // 风向(东北风)
            @SerializedName("wind_sc") val windPower: String, // 风力(3)
            @SerializedName("wind_spd") val windSpeed: String // 风速
        ) : Parcelable {
            @IgnoredOnParcel
            @PrimaryKey(autoGenerate = false)
            var id: Int = TODAY_WEATHER_ID
        }

        @Parcelize
        @Entity(tableName = "weather_time")
        data class Update(
            val loc: String, // 当地时间24H(2019-11-30 16:39)
            val utc: String  // 世界标准时间UTC(2019-11-30 08:39)
        ) : Parcelable {
            @IgnoredOnParcel
            @PrimaryKey(autoGenerate = false)
            var id: Int = WEATHER_TIME_ID
            val zonedDateTime: ZonedDateTime
                get() {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm Z", Locale.getDefault())
                    val timeStamp =
                        dateFormat.parse("$loc ${TimeZone.getDefault().id}")!!.time
                    val instant = Instant.ofEpochSecond(timeStamp)
                    val zoneId = ZoneId.of(TimeZone.getDefault().id)
                    return ZonedDateTime.ofInstant(instant, zoneId)
                }
        }
    }
}