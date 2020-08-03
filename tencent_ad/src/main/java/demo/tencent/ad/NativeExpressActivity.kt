package demo.tencent.ad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.qq.e.ads.nativ.express2.*
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import demo.tencent.ad.O.nativeExpressID
import kotlinx.android.synthetic.main.activity_native_express.*

class NativeExpressActivity : AppCompatActivity(), NativeExpressAD2.AdLoadListener {
    private var nativeExpressAD: NativeExpressAD2? = null
    private var adData: NativeExpressADData2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_express)
        configToolBar(toolbar, this)
        // 创建广告
        nativeExpressAD = NativeExpressAD2(this, nativeExpressID, this)
        nativeExpressAD?.setAdSize(430, 0) // 单位: dp
        nativeExpressAD?.setVideoOption2(
            VideoOption2.Builder()
                .setAutoPlayPolicy(VideoOption2.AutoPlayPolicy.ALWAYS)
                .setAutoPlayMuted(true)     // 自动播放时是否为静音
                .setDetailPageMuted(false)  // 视频详情页是否为静音
                .setMaxVideoDuration(0)     // 设置广告最大时长 5s<=time<=60s (0:无限制)
                .setMinVideoDuration(0)     // 设置广告最小时长 5s<=time<=60s (0:无限制)
                .build()
        )
        nativeExpressAD?.loadAd(1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.native_express_menu, menu)
        menu.findItem(R.id.nativeExpressAD).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nativeExpressList -> {
                startActivity(
                    Intent(
                        this,
                        NativeExpressListActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNoAD(error: AdError) {
        loadingTxT.text = error.errorMsg
        Log.i(TAG, "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

    override fun onLoadSuccess(data: MutableList<NativeExpressADData2>) {
        loadingTxT.visibility = View.GONE
        renderAD(data)
    }

    private fun renderAD(adSet: MutableList<NativeExpressADData2>) {
        if (adSet.size > 0) {
            adContainer.removeAllViews()
            adData = adSet[0]
            Log.i(TAG, "renderAD: eCPM级别: ${adData?.ecpmLevel} 广告时长: ${adData?.videoDuration}")
            adData?.setAdEventListener(object : AdEventListener {
                override fun onClick() {
                    Log.i(TAG, "onClick: $adData")
                }

                override fun onExposed() {
                    Log.i(TAG, "onImpression: $adData")
                }

                override fun onRenderSuccess() {
                    Log.i(TAG, "onRenderSuccess: $adData")
                    adContainer.removeAllViews()
                    if (adData?.adView != null) {
                        adContainer.addView(adData?.adView)
                    }
                }

                override fun onRenderFail() {
                    Log.i(TAG, "onRenderFail: $adData")
                }

                override fun onAdClosed() {
                    Log.i(TAG, "onAdClosed: $adData")
                    adContainer.removeAllViews()
                    adData?.destroy()
                }
            })

            adData?.setMediaListener(object : MediaEventListener {
                override fun onVideoCache() {
                    Log.i(TAG, "onVideoCache: $adData")
                }

                override fun onVideoStart() {
                    Log.i(TAG, "onVideoStart: $adData")
                }

                override fun onVideoResume() {
                    Log.i(TAG, "onVideoResume: $adData")
                }

                override fun onVideoPause() {
                    Log.i(TAG, "onVideoPause: $adData")
                }

                override fun onVideoComplete() {
                    Log.i(TAG, "onVideoComplete: $adData")
                }

                override fun onVideoError() {
                    Log.i(TAG, "onVideoError: $adData")
                }
            })
            adData?.render()
        }
    }

    override fun onDestroy() {
        adData?.destroy()
        super.onDestroy()
    }
}