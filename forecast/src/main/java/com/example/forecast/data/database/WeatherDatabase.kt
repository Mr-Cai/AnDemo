package com.example.forecast.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forecast.data.network.WeatherResponse.WeatherSet.*
import com.example.forecast.data.unit.LocalDateConverter

@Database(
    entities = [Now::class, Basic::class, Update::class, DailyForecast::class],
    version = 1
)
@TypeConverters(LocalDateConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun nowWeatherDAO(): NowWeatherDAO
    abstract fun futureDAO(): FutureDAO
    abstract fun locationDAO(): LocationDAO
    abstract fun timeZoneDAO(): TimeDAO

    companion object {
        @Volatile  // 让数据库对象对其他线程可见
        private var instance: WeatherDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather.db"
            ).build()
    }
}