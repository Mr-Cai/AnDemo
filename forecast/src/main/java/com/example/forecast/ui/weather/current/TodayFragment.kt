package com.example.forecast.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.forecast.R
import com.example.forecast.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.week_fragment.*
import kotlinx.coroutines.GlobalScope
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
    ) = inflater.inflate(R.layout.today_fragment, container, false)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, todayFactory).get(TodayViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {
        val nowWeather = viewModel.weather.await()
        nowWeather.observe(this@TodayFragment, Observer {
            if (it == null) return@Observer
            textView.text = it.toString()
        })
    }

}
