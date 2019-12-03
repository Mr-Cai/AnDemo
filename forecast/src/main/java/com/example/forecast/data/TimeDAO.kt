package com.example.forecast.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.WeatherResponse.WeatherSet.Update

@Dao
interface TimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(updateTime: Update)

    @Query("SELECT * FROM weather_time WHERE id = $WEATHER_TIME_ID")
    fun fetchTimeZoned(): LiveData<Update>
}