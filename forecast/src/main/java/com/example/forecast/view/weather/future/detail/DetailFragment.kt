package com.example.forecast.view.weather.future.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forecast.R
import com.example.forecast.data.unit.ICON
import com.example.forecast.data.unit.LocalDateConverter
import com.example.forecast.data.unit.UnitDetailEntry
import com.example.forecast.internal.DateNotFond
import com.example.forecast.internal.GlideApp
import com.example.forecast.view.base.ScopeFragment
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class DetailFragment : ScopeFragment(), KodeinAware {
    private lateinit var viewModel: DetailViewModel
    private val detailFactory: ((LocalDate) -> DetailFactory) by factory()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.detail_fragment, container, false)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val safeArgs = arguments?.let(fun(it: Bundle) = DetailFragmentArgs.fromBundle(it))
        val date = LocalDateConverter.strToDate(safeArgs?.dateString) ?: throw DateNotFond()
        viewModel = ViewModelProvider(this, detailFactory(date)).get(DetailViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val future = viewModel.weather.await()
        val location = viewModel.weatherLocation.await()
        location.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            (activity as AppCompatActivity).supportActionBar?.title = it.cityName
        })
        future.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            updateDate(it.forecastDate)
            updateAllCondTxT(it)
            GlideApp.with(this@DetailFragment).load("$ICON/${it.condCodeDay}.png")
                .into(condIconPic)
        })
    }

    private fun updateAllCondTxT(it: UnitDetailEntry) {
        nowTempTxT.text = it.tmpMax
    }

    private fun updateDate(date: LocalDate) {
        (activity as AppCompatActivity).supportActionBar?.subtitle =
            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        loadingBar.visibility = View.GONE
        loadingTxT.visibility = View.GONE
    }

    override val kodein by closestKodein()
}
