package com.example.forecast.ui.weather.future.list

import com.example.forecast.R
import com.example.forecast.data.unit.ICON
import com.example.forecast.data.unit.UnitFutureEntry
import com.example.forecast.internal.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_future_weather.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureItem(private val weatherEntry: UnitFutureEntry) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            condTxT.text = weatherEntry.condTxTDay
            updateDate()
            updateTemperature()
            updateIcon()
        }
    }

    override fun getLayout() = R.layout.item_future_weather

    private fun ViewHolder.updateDate() {
        val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        dateTxT.text = weatherEntry.forecastDate.format(dateFormatter)
    }

    private fun ViewHolder.updateTemperature() {
        val unit = "°C" // °F
        val avgTemp = (weatherEntry.tmpMax.toInt() + weatherEntry.tmpMin.toInt()) / 2
        tempTxT.text =
            String.format(
                itemView.context.getString(R.string.avg_temp),
                avgTemp,
                unit
            )
        tempSpanTxT.text = String.format(
            itemView.context.getString(R.string.custom_location),
            weatherEntry.tmpMax, unit,
            weatherEntry.tmpMin, unit
        )
    }

    private fun ViewHolder.updateIcon() {
        GlideApp.with(this.containerView).load("$ICON${weatherEntry.condCodeDay}.png")
            .into(weatherIcon)
    }
}