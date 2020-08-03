package demo.tencent.ad

import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.LayoutParams.MATCH_PARENT
import androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT

object O {
    const val TAG = "腾讯广告调试日志"
    const val appID = "1101152570"
    const val splashID = "8863364436303842593"
    const val rewardID = "6040295592058680"
    const val intersID = "4080298282218338"
    const val bannerID = "4080052898050840"
    const val nativeExpressID = "9061615683013706"
    const val nativeUnifiedID = "4090398440079274"

    fun configToolBar(
        toolbar: Toolbar,
        activity: AppCompatActivity,
        customTitle: String = ""
    ) {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }
        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val params = Toolbar.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                params.gravity = Gravity.CENTER
                view.layoutParams = params
                view.text = customTitle
            }
        }
    }
}