package com.example.forecast.data.unit

import androidx.room.ColumnInfo
import org.threeten.bp.LocalDate

data class UnitFutureEntryImpl(
    @ColumnInfo(name = "forecastDate") override val forecastDate: LocalDate,
    @ColumnInfo(name = "tmpMax") override val tmpMax: String,
    @ColumnInfo(name = "tmpMin") override val tmpMin: String,
    @ColumnInfo(name = "condCodeDay") override val condCodeDay: String,
    @ColumnInfo(name = "condTxTDay") override val condTxTDay: String
) : UnitFutureEntry