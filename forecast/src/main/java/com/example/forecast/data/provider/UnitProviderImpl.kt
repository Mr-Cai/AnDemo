package com.example.forecast.data.provider

import android.content.Context
import com.example.forecast.R
import com.example.forecast.internal.UnitSystem

class UnitProviderImpl(val context: Context) : PrefProvider(context), UnitProvider {
    override fun getUnitSystem(): UnitSystem {
        val selectName = preferences.getString(
            context.getString(R.string.unit_system),
            UnitSystem.METRIC.name
        )
        return UnitSystem.valueOf(selectName!!)
    }
}

