package com.example.forecast.view.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forecast.R
import com.example.forecast.R.string.*
import com.example.forecast.data.unit.ICON
import com.example.forecast.internal.GlideApp
import com.example.forecast.view.base.ScopeFragment
import kotlinx.android.synthetic.main.now_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class TodayFragment : ScopeFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val todayFactory by instance<TodayFactory>()
    private lateinit var viewModel: TodayViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.now_fragment, container, false)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, todayFactory).get(TodayViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {
        val nowWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()
        nowWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            loadingBar.visibility = View.GONE
            loadingTxT.visibility = View.GONE
            updateDateSubTitle()
            updateTemperatures(it.temperature, it.feelTemp)
            updateCondition(it.conditionDesc)
            updatePrecipitation(it.precipitation)
            updateWind(it.windSpeed, it.windDir)
            updateVisibility(it.visibility)
            GlideApp.with(this@TodayFragment)
                .load("$ICON/${it.conditionCode}.png")
                .into(condIconPic)
        })
        weatherLocation.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            updateLocationTitle(it.cityName)
        })
    }

    private fun chooseUnitAbbreviation(metric: String, imperial: String) =
        if (viewModel.isMetric) metric else imperial

    private fun formatString(resID: Int, vararg args: Any?) =
        String.format(getString(resID), *args)

    private fun updateLocationTitle(location: String) {
        (activity as AppCompatActivity).supportActionBar!!.title = location
    }

    private fun updateDateSubTitle() {
        (activity as AppCompatActivity).supportActionBar!!.subtitle = "今日"
    }

    private fun updateTemperatures(temp: String, feel: String) {
        val unit = chooseUnitAbbreviation("℃", "℉")
        nowTempTxT.text = formatString(temperature, temp, unit)
        feelTempTxT.text = formatString(fell_temperature, feel, unit)
    }

    private fun updateCondition(condition: String) {
        condTxT.text = condition
    }

    private fun updateWind(windSpeed: String, windDir: String) {
        val unit = chooseUnitAbbreviation("km/h", "mile/h")
        windDesc.text = formatString(wind_desc, windSpeed, unit, windDir)
    }

    private fun updatePrecipitation(precipitation: String) {
        precipitationTxT.text = formatString(precipitation_txt, precipitation, "mm")
    }

    private fun updateVisibility(visibility: String) {
        val unit = chooseUnitAbbreviation("km", "mile")
        visibilityTxT.text = formatString(visibility_txt, visibility, unit)
    }
}
