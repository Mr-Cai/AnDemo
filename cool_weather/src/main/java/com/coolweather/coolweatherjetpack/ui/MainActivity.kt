package com.coolweather.coolweatherjetpack.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.ui.area.ChooseAreaFragment
import com.coolweather.coolweatherjetpack.ui.weather.WeatherActivity
import com.coolweather.coolweatherjetpack.util.InjectorUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (KEY.isEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("请先配置API_KEY")
                setCancelable(true)
                setPositiveButton("OK") { _, _ ->
                    finish()
                }
                show()
            }
        } else {
            val viewModel = ViewModelProviders.of(this, InjectorUtil.getMainModelFactory())
                .get(MainViewModel::class.java)
            if (viewModel.isWeatherCached()) {
                val intent = Intent(this, WeatherActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ChooseAreaFragment()).commit()
            }
        }
    }

    companion object {
        const val KEY = "985cb464f7ae4866a1fc35fd63e17e42"
    }
}
