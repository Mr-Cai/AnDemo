package com.example.forecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.network.WeatherResponse.WeatherSet.DailyForecast
import com.example.forecast.data.unit.ImperialFutureEntry
import com.example.forecast.data.unit.MetricFutureEntry
import com.example.forecast.data.unit.UnitDetailEntryImpl
import org.threeten.bp.LocalDate

@Dao
interface FutureDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(forecast: List<DailyForecast>)   // 插入未来天气数据至本地数据库

    // 查询自今日的未来天气 公制(m: ℃)
    @Query("SELECT * FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun queryFutureMetric(startDate: LocalDate): LiveData<List<MetricFutureEntry>>

    // 查询自今日的未来天气 英制(i: ℉)
    @Query("SELECT * FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun queryFutureImperial(startDate: LocalDate): LiveData<List<ImperialFutureEntry>>

    @Query("SELECT * FROM future_weather WHERE date(forecastDate) = date(:startDate)")
    fun queryDetail(startDate: LocalDate): LiveData<UnitDetailEntryImpl> // 查询指定日期天气详情

    @Query("SELECT count(id) FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun countFuture(startDate: LocalDate): Int  // 计算未来天气记录条数

    @Query("DELETE FROM future_weather WHERE date(forecastDate) < date(:firstDateToKeep)")
    fun deleteFuture(firstDateToKeep: LocalDate)   // 删除第一条日期的天气记录
}