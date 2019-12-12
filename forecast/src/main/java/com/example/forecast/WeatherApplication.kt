package com.example.forecast

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.forecast.data.database.WeatherDatabase
import com.example.forecast.data.network.*
import com.example.forecast.data.provider.LocationProvider
import com.example.forecast.data.provider.LocationProviderImpl
import com.example.forecast.data.provider.UnitProvider
import com.example.forecast.data.provider.UnitProviderImpl
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.data.repository.WeatherRepositoryImpl
import com.example.forecast.ui.weather.current.TodayFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

@Suppress("unused")
class WeatherApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@WeatherApplication))

        bind() from singleton { WeatherDatabase(instance()) }
        bind() from singleton { WeatherService(instance()) }

        bind() from singleton { instance<WeatherDatabase>().nowWeatherDAO() }
        bind() from singleton { instance<WeatherDatabase>().locationDAO() }
        bind() from singleton { instance<WeatherDatabase>().timeZoneDAO() }

        bind() from provider { TodayFactory(instance(), instance()) }

        bind<NetworkInterceptor>() with singleton { NetworkInterceptorImpl(instance()) }
        bind<NetworkDataSource>() with singleton { NetworkDataSourceImpl(instance()) }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl() }
        bind<WeatherRepository>() with singleton {
            WeatherRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
    }
}