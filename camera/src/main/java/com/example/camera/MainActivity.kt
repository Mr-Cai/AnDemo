package com.example.camera

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        captureBTN.setOnClickListener {
            Dialog(this).apply {
                setContentView(R.layout.hello)
                show()
            }
        }
    }
}
