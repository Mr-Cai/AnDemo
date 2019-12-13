package com.example.forecast.data.unit

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object LocalDateConverter {
    @TypeConverter
    @JvmStatic
    fun strToDate(strDate: String?) = strDate?.let {
        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    @JvmStatic
    fun dateToStr(dateTime: LocalDate?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)
}