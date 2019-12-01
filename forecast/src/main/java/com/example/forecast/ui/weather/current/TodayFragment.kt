package com.example.forecast.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.forecast.R
import com.example.forecast.data.WeatherService
import com.example.forecast.data.network.NetworkDataSourceImpl
import com.example.forecast.data.network.NetworkInterceptorImpl
import kotlinx.android.synthetic.main.week_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TodayFragment : Fragment() {

    companion object {
        fun newInstance() = TodayFragment()
    }

    private lateinit var viewModel: TodayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.today_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TodayViewModel::class.java)
        val weatherService = WeatherService(NetworkInterceptorImpl(this.context!!))
        val weatherDataSource = NetworkDataSourceImpl(weatherService)
        weatherDataSource.downloaderNowWeather.observe(this, Observer {
            textView.text = it.toString()
        })
        GlobalScope.launch(Dispatchers.Main) {
            weatherDataSource.getTodayWeatherAsync("深圳")
        }
    }

}
