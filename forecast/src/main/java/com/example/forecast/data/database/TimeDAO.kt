package com.example.forecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.unit.WEATHER_TIME_ID
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Update

@Dao
interface TimeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(update: Update)

    @Query("SELECT * FROM weather_time WHERE id = $WEATHER_TIME_ID")
    fun getTimeZone(): LiveData<Update>
}