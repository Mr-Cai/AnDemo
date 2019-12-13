package com.example.forecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.network.WeatherResponse.WeatherSet.DailyForecast
import com.example.forecast.data.unit.UnitFutureEntryImpl
import org.threeten.bp.LocalDate

@Dao
interface FutureDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(forecast: List<DailyForecast>)

    @Query("SELECT * FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun getFuture(startDate: LocalDate): LiveData<List<UnitFutureEntryImpl>>  // 获取从今天起的未来天气

    @Query("SELECT count(id) FROM future_weather WHERE date(forecastDate) >= date(:startDate)")
    fun countFuture(startDate: LocalDate): Int

    @Query("DELETE FROM future_weather WHERE date(forecastDate) < date(:firstDateToKeep)")
    fun deleteFuture(firstDateToKeep: LocalDate)    // 删除第一条日期的天气记录
}