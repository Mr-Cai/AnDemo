package com.example.tencent_ads

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val list = ArrayList<TypeAdapter.Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.add(TypeAdapter.Item("插屏广告", R.drawable.ic_interstial_ads))
        list.add(TypeAdapter.Item("Banner广告", R.drawable.ic_banner_ads))
        list.add(TypeAdapter.Item("原生广告", R.drawable.ic_origin_ads))
        adTypeRecycler.adapter = TypeAdapter(list)
        adTypeRecycler.layoutManager = GridLayoutManager(this, 2)
    }
}
