package com.example.forecast.data.unit

import androidx.room.ColumnInfo

class UnitFutureEntryImpl(
    @ColumnInfo(name = "forecastDate") override val forecastDate: String,
    @ColumnInfo(name = "tmpMax") override val tmpMax: String,
    @ColumnInfo(name = "tmpMin") override val tmpMin: String,
    @ColumnInfo(name = "condCodeDay") override val condCodeDay: String,
    @ColumnInfo(name = "condTxTDay") override val condTxTDay: String
) : UnitFutureEntry