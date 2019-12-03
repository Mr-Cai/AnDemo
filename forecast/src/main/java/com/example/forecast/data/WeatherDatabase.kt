package com.example.forecast.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.forecast.data.network.WeatherResponse.WeatherSet.Now

@Database(entities = [Now::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun nowWeatherDAO(): NowWeatherDAO

    companion object {
        @Volatile  // 让数据库对象对其他线程可见
        private var instance: WeatherDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather.db"
            ).build()
    }
}