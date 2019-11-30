package com.example.forecast.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.WeatherResponse.WeatherSet.Now
import com.example.forecast.data.unit.ImperialNowEntry
import com.example.forecast.data.unit.MetricNowEntry

@Dao
interface NowWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(nowEntry: Now)

    @Query("SELECT * FROM today_weather WHERE id = $TODAY_WEATHER_ID")
    fun getWeatherMetric(): LiveData<MetricNowEntry>

    @Query("SELECT * FROM today_weather WHERE id = $TODAY_WEATHER_ID")
    fun getWeatherImperial(): LiveData<ImperialNowEntry>
}