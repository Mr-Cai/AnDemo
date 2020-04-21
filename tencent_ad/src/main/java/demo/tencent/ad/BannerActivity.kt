package demo.tencent.ad

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qq.e.ads.banner2.UnifiedBannerADListener
import com.qq.e.ads.banner2.UnifiedBannerView
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_banner.*
import kotlin.math.roundToInt

class BannerActivity : AppCompatActivity(), UnifiedBannerADListener {
    private lateinit var bannerView: UnifiedBannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        configToolBar(toolbar, this)
        loadBanner().loadAD()
        showBannerBTN.setOnClickListener {
            bannerView.destroy()
            bannerView.loadAD()
        }
    }

    private fun loadBanner(): UnifiedBannerView {
        bannerView = UnifiedBannerView(this, O.appID, O.bannerID, this)
        containerF.addView(bannerView, bannerParams())
        return bannerView
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 ,
     * 开发者可自行设置符合规定宽高比的具体宽度和高度值
     */
    private fun bannerParams(): FrameLayout.LayoutParams? {
        val screenSize = Point()
        windowManager.defaultDisplay.getSize(screenSize)
        return FrameLayout.LayoutParams(screenSize.x, (screenSize.x / 6.4F).roundToInt())
    }

    override fun onADCloseOverlay() {
        Log.i(TAG, "onADCloseOverlay: ")
    }

    override fun onADExposure() {
        Log.i(TAG, "onADExposure: ")
    }

    override fun onADClosed() {
        Handler().postDelayed({
            loadBanner().loadAD()
        }, 2000)
        Log.i(TAG, "onADClosed: ")
    }

    override fun onADLeftApplication() {
        Log.i(TAG, "onADLeftApplication: ")
    }

    override fun onADOpenOverlay() {
        Log.i(TAG, "onADOpenOverlay: ")
    }

    override fun onNoAD(error: AdError) {
        Handler().postDelayed({
            loadBanner().loadAD()
        }, 2000)
        Log.i(TAG, "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

    override fun onADReceive() {
        Toast.makeText(this, "横幅广告加载成功", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onADReceive: ")
    }

    override fun onADClicked() {
        Log.i(TAG, "onADClicked: ")
    }
}