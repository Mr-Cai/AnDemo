package com.example.forecast.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.forecast.R
import com.example.forecast.data.unit.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {
    private lateinit var navController: NavController   // 导航菜单控制器
    private val locClient: FusedLocationProviderClient by instance()
    private val locationID = 0xc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null
        bottomNav.itemIconTintList = null  // 去除图标渲染
        navController = Navigation.findNavController(this, R.id.navContainerF)
        bottomNav.setupWithNavController(navController) // 底部菜单绑定标签页控制器
        NavigationUI.setupActionBarWithNavController(this, navController)   // 操作栏绑定控制器
        requestLocPermission()
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) -> bindLocManager()
            else -> requestLocPermission()
        }
    }

    private fun requestLocPermission() = ActivityCompat.requestPermissions(
        this,
        arrayOf(ACCESS_COARSE_LOCATION),
        locationID
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            locationID -> {
                when {
                    grantResults.isNotEmpty() && grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED -> bindLocManager()
                    else -> toast(this, "请到应用设置开启相应权限")
                }
            }
        }
    }

    private fun bindLocManager() {
        BindLocManager(this, locClient, object : LocationCallback() {})
    }

    // 顶部导航箭头回调(无抽屉)
    override fun onSupportNavigateUp(): Boolean = NavigationUI.navigateUp(navController, null)

    override val kodein: Kodein by closestKodein()
}
