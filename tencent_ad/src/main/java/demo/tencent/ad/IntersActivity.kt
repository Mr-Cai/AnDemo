package demo.tencent.ad

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_inters.*

class IntersActivity : AppCompatActivity(),
    UnifiedInterstitialADListener,
    UnifiedInterstitialMediaListener {
    private lateinit var intersAD: UnifiedInterstitialAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inters)
        configToolBar(toolbar, this)
        loadInterAD().loadAD()
        showIntersBTN.setOnClickListener {
            intersAD.showAsPopupWindow(this)
        }
    }

    private fun loadInterAD(): UnifiedInterstitialAD {
        intersAD = UnifiedInterstitialAD(
            this,
            O.appID,
            O.intersID,
            this
        )
        intersAD.run {
            close()
            destroy()
            null
        }
        setVideoOption()
        return intersAD
    }

    private fun setVideoOption() {
        val option = VideoOption.Builder()
            .setAutoPlayMuted(true)
            .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS)
            .setDetailPageMuted(true)
            .build()
        intersAD.run {
            setVideoOption(option)
            setMinVideoDuration(15)
            setMaxVideoDuration(60)
            setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        intersAD.destroy()
    }


    override fun onADExposure() {
        Log.i(TAG, "onADExposure: ")
    }

    override fun onVideoCached() {
        Log.i(TAG, "onVideoCached: ")
    }

    override fun onADOpened() {
        Log.i(TAG, "onADOpened: ")
    }

    override fun onADClosed() {
        intersAD.close()
        intersAD.destroy()
        loadInterAD().loadAD()
        Log.i(TAG, "onADClosed: ")
    }

    override fun onADLeftApplication() {
        Log.i(TAG, "onADLeftApplication: ")
    }

    override fun onADReceive() {
        Toast.makeText(this, "插屏广告加载成功", Toast.LENGTH_SHORT).show()
        if (intersAD.adPatternType == AdPatternType.NATIVE_VIDEO) {
            intersAD.setMediaListener(this)
        }
        Log.i(TAG, "onADReceive: ")
    }

    override fun onNoAD(error: AdError) {
        Toast.makeText(
            this,
            "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}",
            Toast.LENGTH_SHORT
        ).show()
        Handler().postDelayed({
            loadInterAD().loadAD()
        }, 2000)
        Log.i(TAG, "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

    override fun onADClicked() {
        Log.i(TAG, "onADClicked: ")
    }

    override fun onVideoPageOpen() {
        Log.i(TAG, "onVideoPageOpen: ")
    }

    override fun onVideoLoading() {
        Log.i(TAG, "onVideoLoading: ")
    }

    override fun onVideoReady(time: Long) {
        Log.i(TAG, "onVideoReady: $time")
    }

    override fun onVideoInit() {
        Log.i(TAG, "onVideoInit: ")
    }

    override fun onVideoPause() {
        Log.i(TAG, "onVideoPause: ")
    }

    override fun onVideoPageClose() {
        Log.i(TAG, "onVideoPageClose: ")
    }

    override fun onVideoStart() {
        Log.i(TAG, "onVideoStart: ")
    }

    override fun onVideoComplete() {
        Log.i(TAG, "onVideoComplete: ")
    }

    override fun onVideoError(error: AdError) {
        Log.i(TAG, "onVideoError: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

}