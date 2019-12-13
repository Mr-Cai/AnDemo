package com.example.forecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.network.WeatherResponse.WeatherSet.DailyForecast
import com.example.forecast.data.unit.UnitDetailEntryImpl
import com.example.forecast.data.unit.UnitFutureEntryImpl
import org.threeten.bp.LocalDate

@Dao
interface FutureDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(forecast: List<DailyForecast>)   // 插入未来天气数据至本地数据库

    @Query("SELECT * FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun queryFuture(startDate: LocalDate): LiveData<List<UnitFutureEntryImpl>> // 查询自今日的未来天气

    @Query("SELECT * FROM future_weather WHERE date(forecastDate) = date(:startDate)")
    fun queryDetail(startDate: LocalDate): LiveData<List<UnitDetailEntryImpl>> // 查询指定日期天气详情

    @Query("SELECT count(id) FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun countFuture(startDate: LocalDate): Int  // 计算未来天气记录条数

    @Query("DELETE FROM future_weather WHERE date(forecastDate) < date(:firstDateToKeep)")
    fun deleteFuture(firstDateToKeep: LocalDate)   // 删除第一条日期的天气记录
}