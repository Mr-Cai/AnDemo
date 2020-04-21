package demo.tencent.ad

import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.LayoutParams.MATCH_PARENT
import androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT

object O {
    const val TAG = "腾讯广告调试日志"
    const val appID = "1109716769"
    const val splashID = "7020785136977336"
    const val rewardID = "6021002701726334"
    const val intersID = "2041008945668154"
    const val bannerID = "9040882216019714"
    const val nativeID = "8041808915486340"

    fun configToolBar(
        toolbar: Toolbar,
        activity: AppCompatActivity,
        customTitle: String = ""
    ) {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
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