package com.demo.coil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView1.load("https://pic.downk.cc/item/5f0d8c0b14195aa594dcb68f.png")

        imageView2.load("https://pic.downk.cc/item/5f0d1c6f14195aa594bbee6c.png") {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            transformations(CircleCropTransformation())
        }
    }
}