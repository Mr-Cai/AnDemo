package com.example.forecast.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.forecast.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController   // 导航菜单控制器
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null
        bottomNav.itemIconTintList = null  // 去除图标渲染
        navController = Navigation.findNavController(this, R.id.navContainerF)
        bottomNav.setupWithNavController(navController) // 底部菜单绑定标签页控制器
        NavigationUI.setupActionBarWithNavController(this, navController)   // 操作栏绑定控制器
    }

    // 顶部导航箭头回调(无抽屉)
    override fun onSupportNavigateUp(): Boolean = NavigationUI.navigateUp(navController, null)
}
