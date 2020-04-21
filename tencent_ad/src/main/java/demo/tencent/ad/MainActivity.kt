package demo.tencent.ad

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*

class MainActivity : AppCompatActivity() {
    private val typeList = ArrayList<TypeAdapter.Item>()
    private var gridCount: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (SDK_INT >= 23) checkAndRequestPermission()
        if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
            gridCount = 6
        }
        gridRecycler.layoutManager = GridLayoutManager(this, gridCount)
        typeList.add(TypeAdapter.Item("激励视频", R.drawable.ic_reward_video))
        typeList.add(TypeAdapter.Item("插屏广告", R.drawable.ic_interstial_ads))
        typeList.add(TypeAdapter.Item("横幅广告", R.drawable.ic_banner_ads))
        typeList.add(TypeAdapter.Item("原生广告", R.drawable.ic_origin_ads))
        typeList.add(TypeAdapter.Item("闪屏广告", R.drawable.ic_splash_ad))
        gridRecycler.adapter = TypeAdapter(typeList)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkAndRequestPermission() {
        val lackedPermission = ArrayList<String>()
        // 检查缺失的权限
        when {
            checkSelfPermission(READ_PHONE_STATE) != PERMISSION_GRANTED -> {
                lackedPermission.add(READ_PHONE_STATE)
            }
            checkSelfPermission(ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED -> {
                lackedPermission.add(ACCESS_COARSE_LOCATION)
            }
        }

        if (lackedPermission.size != 0) {  // 集合缺失的权限
            val requestPermissions = arrayOfNulls<String>(lackedPermission.size)
            lackedPermission.toArray<String>(requestPermissions)
            requestPermissions(requestPermissions, 1024)
        }
    }

    private fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1024 && !hasAllPermissionsGranted(grantResults)) {
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show()
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    class TypeAdapter(private val list: ArrayList<Item>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item,
                parent,
                false
            )
        )

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemView = holder.itemView
            itemView.nameTxT.text = list[position].name
            itemView.iconPic.setImageResource(list[position].icon)
            itemView.setOnClickListener {
                when (position) {
                    0 -> itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            RewardActivity::class.java
                        )
                    )
                    1 -> itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            IntersActivity::class.java
                        )
                    )
                    2 -> itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            BannerActivity::class.java
                        )
                    )
                    3 -> itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            NativeADActivity::class.java
                        )
                    )
                    4 -> itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            SplashActivity::class.java
                        )
                    )
                }
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        data class Item(val name: String, val icon: Int)
    }

}