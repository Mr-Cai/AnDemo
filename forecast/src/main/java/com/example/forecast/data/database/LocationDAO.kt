package com.example.forecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Basic
import com.example.forecast.data.unit.WEATHER_BASIC_ID

@Dao
interface LocationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(location: Basic)

    @Query("SELECT * FROM weather_basic WHERE id = $WEATHER_BASIC_ID")
    fun getLocation(): LiveData<Basic>

    @Query("SELECT * FROM weather_basic WHERE id = $WEATHER_BASIC_ID")
    fun getLocationNonLive(): Basic?
}