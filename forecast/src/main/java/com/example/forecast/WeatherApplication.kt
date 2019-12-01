package com.example.forecast

import android.app.Application
import com.example.forecast.data.WeatherDatabase
import com.example.forecast.data.WeatherService
import com.example.forecast.data.network.NetworkDataSource
import com.example.forecast.data.network.NetworkDataSourceImpl
import com.example.forecast.data.network.NetworkInterceptor
import com.example.forecast.data.network.NetworkInterceptorImpl
import com.example.forecast.data.repository.WeatherRepository
import com.example.forecast.data.repository.WeatherRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@Suppress("unused")
class WeatherApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@WeatherApplication))
        bind() from singleton { WeatherDatabase(instance()) }
        bind() from singleton { instance<WeatherDatabase>().nowWeatherDAO() }
        bind<NetworkInterceptor>() with singleton { NetworkInterceptorImpl(instance()) }
        bind() from singleton { WeatherService(instance()) }
        bind<NetworkDataSource>() with singleton { NetworkDataSourceImpl(instance()) }
        bind<WeatherRepository>() with singleton { WeatherRepositoryImpl(instance(), instance()) }
    }
}