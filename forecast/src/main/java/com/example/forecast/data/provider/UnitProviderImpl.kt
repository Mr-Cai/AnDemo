package com.example.forecast.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.forecast.R
import com.example.forecast.internal.UnitSystem

class UnitProviderImpl(context: Context) : UnitProvider {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun getUnitSystem(): UnitSystem {
        val selectName = preferences.getString(
            appContext.getString(R.string.unit_system),
            UnitSystem.METRIC.name
        )
        return UnitSystem.valueOf(selectName!!)
    }
}

