package com.example.tencent_ads

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.type_item.view.*

class TypeAdapter(private val list: ArrayList<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.type_item,
            parent,
            false
        )
    )

    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        itemView.nameTxT.text = list[position].name
        itemView.iconPic.run {
            setImageResource(list[position].icon)
            setOnClickListener {
                when (list[position].name) {
                    "插屏广告" -> {
                        context.startActivity(
                            Intent(
                                context,
                                UnifiedInterstitialADActivity::class.java
                            )
                        )
                    }
                    "Banner广告" -> {
                        context.startActivity(Intent(context, UnifiedBannerActivity::class.java))
                    }
                    "原生广告" -> {
                        context.startActivity(Intent(context, NativeExpressADActivity::class.java))
                    }
                }
            }
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    data class Item(val name: String, val icon: Int)
}

