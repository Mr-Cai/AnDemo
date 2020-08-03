package demo.tencent.ad

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(), SplashADListener {
    private lateinit var splashAD: SplashAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        configToolBar(toolbar, this)
        splashAD = SplashAD(this, O.splashID, this)
        showSplash.setOnClickListener {
            toolbar.visibility = View.GONE

            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

            splashAD.run {
                preLoad()
                fetchAndShowIn(splashADContainer)
            }
        }
    }

    override fun onADExposure() {
        Log.i(TAG, "onADExposure: ")
    }

    override fun onADDismissed() {
        finish()
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        toolbar.visibility = View.VISIBLE
        Log.i(TAG, "onADDismissed: ")
    }

    override fun onADPresent() {
        Log.i(TAG, "onADPresent: ")
    }

    override fun onADLoaded(time: Long) {
        Log.i(TAG, "onADLoaded: $time")
    }

    override fun onNoAD(error: AdError) {
        Log.i(TAG, "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

    override fun onADClicked() {
        Log.i(TAG, "onADClicked: ")
    }

    override fun onADTick(tick: Long) {
        Log.i(TAG, "onADTick: $tick")
    }
}