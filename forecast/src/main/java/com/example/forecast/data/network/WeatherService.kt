package com.example.forecast.data.network

import com.example.forecast.data.unit.API_KEY
import com.example.forecast.data.unit.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 开发文档 https://dev.heweather.com/docs/api/weather
// 示例 https://free-api.heweather.net/s6/weather/now?location=shenzhen&key=985cb464f7ae4866a1fc35fd63e17e42
interface WeatherService {
    @GET("now.json")    // 今日天气
    fun getCurrentAsync(
        @Query("location") cityName: String, // 查询城市
        @Query("lang") langCode: String = "zh", // 语言(默认中文)
        @Query("unit") unit: String = "m" // 单位(m:公制(默认) i:英制)
    ): Deferred<WeatherResponse>   // 天气数据类

    @GET("forecast.json")   // 未来一周
    fun getForecastAsync(
        @Query("location") location: String, // 查询城市
        @Query("lang") langCode: String = "zh", // 语言(默认中文)
        @Query("unit") unit: String = "m" // 单位(m:公制(默认) i:英制)
    ): Deferred<WeatherResponse>

    companion object {
        operator fun invoke(networkInterceptor: NetworkInterceptor): WeatherService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(networkInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
        }
    }

}
