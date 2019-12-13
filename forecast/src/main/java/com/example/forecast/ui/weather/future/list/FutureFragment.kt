package com.example.forecast.ui.weather.future.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forecast.R
import com.example.forecast.data.unit.UnitFutureEntry
import com.example.forecast.data.unit.toast
import com.example.forecast.ui.base.ScopeFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.future_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureFragment : ScopeFragment(), KodeinAware {
    private lateinit var viewModel: FutureViewModel
    private val futureFactory: FutureFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.future_fragment, container, false)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FutureViewModel::class.java)
        bindUI()
    }

    override val kodein by closestKodein()

    private fun bindUI() = launch(Dispatchers.Main) {
        val futureEntry = viewModel.future.await()
        val weatherLocation = viewModel.weatherLocation.await()
        weatherLocation.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            updateLocation(it.cityName)
        })
        futureEntry.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            loadView.visibility = View.GONE
            updateNextWeek()
            initRecyclerView(it.toFutureItem())
        })
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateNextWeek() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "最近一周"
    }

    private fun List<UnitFutureEntry>.toFutureItem(): List<FutureItem> = this.map {
        FutureItem(it)
    }

    private fun initRecyclerView(items: List<FutureItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }
        futureRecycler.apply {
            layoutManager = LinearLayoutManager(this@FutureFragment.context)
            adapter = groupAdapter
        }
        groupAdapter.setOnItemClickListener { e, _ ->
            toast(context!!, e.id.toString())
        }
    }
}
