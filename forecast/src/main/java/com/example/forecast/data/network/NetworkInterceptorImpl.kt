package com.example.forecast.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import androidx.annotation.RequiresApi
import com.example.forecast.data.internal.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptorImpl(context: Context) : NetworkInterceptor {
    private val appContext = context.applicationContext
    private val netManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun intercept(chain: Interceptor.Chain): Response {
        when {
            SDK_INT >= M -> if (!isActive()) throw NoConnectivityException()
            SDK_INT < M -> if (!isOnline()) throw NoConnectivityException()
        }
        if (!isOnline()) throw NoConnectivityException()
        return chain.proceed(chain.request())
    }

    @Suppress("DEPRECATION")    // 在 Android Q 中被弃用, 旧版未弃用
    private fun isOnline() = netManager.activeNetworkInfo?.isConnected ?: false

    @RequiresApi(M)     // 在Android M 及以上使用
    private fun isActive(): Boolean {
        val network = netManager.activeNetwork
        return if (network != null) {
            netManager.getNetworkCapabilities(network)!!.hasCapability(NET_CAPABILITY_INTERNET)
        } else false
    }
}
