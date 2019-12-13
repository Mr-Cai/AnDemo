package com.example.forecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Now
import com.example.forecast.data.unit.NowEntry
import com.example.forecast.data.unit.TODAY_WEATHER_ID

@Dao
interface NowWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(nowEntry: Now)

    @Query("SELECT * FROM today_weather WHERE id = $TODAY_WEATHER_ID")
    fun fetchNowWeather(): LiveData<NowEntry>
}