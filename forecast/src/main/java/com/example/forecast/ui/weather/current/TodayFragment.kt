package com.example.forecast.ui.weather.current

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.forecast.R
import com.example.forecast.data.WeatherService
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

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TodayViewModel::class.java)
        val weatherService = WeatherService()
        GlobalScope.launch(Dispatchers.Main) {
            val todayWeather = weatherService.getTodayWeatherAsync("深圳").await()
//            textView.text = "今日温度:${todayWeather.weatherSet[0].now.feelTemp}"
            textView.text = todayWeather.toString()
        }
    }

}
