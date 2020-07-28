package demo.tencent.ad

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.*
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_ad_native.*

class NativeADActivity : AppCompatActivity(), NativeADUnifiedListener {
    private var adData: NativeUnifiedADData? = null // 广告数据
    private val adHandler: Handler by lazy { getHandler() } // 广告接收处理
    private lateinit var nativeUnifiedAD: NativeUnifiedAD  // 广告UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_native)
        configToolBar(toolbar, this)
        loadAd()
        nativeShowBTN.setOnClickListener {
            adData?.destroy()
            nativeUnifiedAD.loadData(1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.finish, menu)
        return true
    }

    private fun loadAd() {
        nativeUnifiedAD = NativeUnifiedAD(this, O.appID, O.nativeID, this)
        nativeUnifiedAD.run {
            setMinVideoDuration(0)
            setMaxVideoDuration(0)
            setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
            setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK)
            loadData(1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nativeRender -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        adData?.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        adData?.destroy()
    }

    private fun getHandler() = Handler(Handler.Callback { msg ->
        when (msg.what) {
            MSG_INIT_AD -> {
                val ad = msg.obj as NativeUnifiedADData
                initAd(ad)
            }
            MSG_VIDEO_START -> {
                adPoster.visibility = View.GONE
                adMediaView.visibility = View.VISIBLE
            }
        }
        true
    })

    override fun onADLoaded(ads: MutableList<NativeUnifiedADData>) {
        if (ads.isNotEmpty()) {
            val msg = Message.obtain()
            msg.what = MSG_INIT_AD
            adData = ads[0]
            msg.obj = adData
            adHandler.sendMessage(msg)
        }
    }

    override fun onNoAD(error: AdError) {
        Log.i(O.TAG, "onADError: 错误编号:${error.errorCode}，错误消息:${error.errorMsg}")
    }

    private fun initAd(ad: NativeUnifiedADData) {
        if (ad.adPatternType == AdPatternType.NATIVE_VIDEO) {
            Toast.makeText(this, "正在加载视频素材", Toast.LENGTH_SHORT).show()
            ad.preloadVideo(object : VideoPreloadListener {
                override fun onVideoCached() {
                    Log.i(O.TAG, "onVideoCached: ")
                    showAd(ad)
                }

                override fun onVideoCacheFailed(errorNo: Int, msg: String) {
                    Log.i(O.TAG, "onVideoCacheFailed: ")
                }
            })
            showAd(ad)
        }
        showAd(ad)
    }

    private fun showAd(adData: NativeUnifiedADData) {
        adRenderUI(adData)
        adData.bindAdToView(
            this, adNativeContainer, null,
            mutableListOf(adPoster, adMediaView, topBarLayout)
        )
        if (adData.adPatternType == AdPatternType.NATIVE_VIDEO) {
            adHandler.sendEmptyMessage(MSG_VIDEO_START)
            adData.bindMediaView(
                adMediaView,
                getVideoOption(),
                object : NativeADMediaListener {
                    override fun onVideoInit() {
                        Log.i(O.TAG, "onVideoInit: ")
                    }

                    override fun onVideoLoading() {
                        Log.i(O.TAG, "onVideoLoading: ")
                    }

                    override fun onVideoReady() {
                        Log.i(O.TAG, "onVideoReady: ")
                    }

                    override fun onVideoLoaded(videoDuration: Int) {
                        Log.i(O.TAG, "onVideoLoaded: $videoDuration")
                    }

                    override fun onVideoStart() {
                        Log.i(O.TAG, "onVideoStart: ")
                    }

                    override fun onVideoPause() {
                        Log.i(O.TAG, "onVideoPause: ")
                    }

                    override fun onVideoResume() {
                        Log.i(O.TAG, "onVideoResume: ")
                    }

                    override fun onVideoCompleted() {
                        Log.i(O.TAG, "onVideoCompleted: ")
                    }

                    override fun onVideoError(error: AdError) {
                        Log.i(O.TAG, "onVideoError: ")
                    }

                    override fun onVideoStop() {
                        Log.i(O.TAG, "onVideoStop: ")
                    }

                    override fun onVideoClicked() {
                        Log.i(O.TAG, "onVideoClicked: ")
                    }
                })
        } else if (adData.adPatternType == AdPatternType.NATIVE_2IMAGE_2TEXT ||
            adData.adPatternType == AdPatternType.NATIVE_1IMAGE_2TEXT
        ) {
            adData.bindAdToView(
                this, adNativeContainer, null,
                mutableListOf(adPoster, topBarLayout)
            )
        }
        adData.setNativeAdEventListener(object : NativeADEventListener {
            override fun onADExposed() {
                Log.i(O.TAG, "onADExposed: ")
            }

            override fun onADClicked() {
                Log.i(O.TAG, "onADClicked: ${NativeUnifiedADData.ext["clickUrl"]}")
            }

            override fun onADError(error: AdError) {
                Log.i(O.TAG, "onADError: 错误编号:${error.errorCode}，错误消息:${error.errorMsg}")
            }

            override fun onADStatusChanged() {
                Log.i(O.TAG, "onADStatusChanged: ")
            }
        })
    }

    private fun adRenderUI(ad: NativeUnifiedADData) {
        val patternType = ad.adPatternType
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
            || patternType == AdPatternType.NATIVE_VIDEO
        ) {
            adPoster.visibility = View.VISIBLE
            Glide.with(this).load(ad.iconUrl).into(logoPic)
            Glide.with(this).load(ad.imgUrl).into(adPoster)
            titleTxTAD.text = ad.title
            descTxTAD.text = ad.desc
        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            Glide.with(this).load(ad.iconUrl).into(logoPic)
            Glide.with(this).load(ad.imgUrl).into(adPoster)
            titleTxTAD.text = ad.title
            descTxTAD.text = ad.desc
        }
    }

    private fun getVideoOption() = VideoOption.Builder()
        .run {
            setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS)
            setAutoPlayMuted(true)
            setDetailPageMuted(false)
            setNeedCoverImage(true)
            setNeedProgressBar(true)
            setEnableDetailPage(true)
            setEnableUserControl(false)
            build()
        }

    companion object {
        private const val MSG_INIT_AD = 0
        private const val MSG_VIDEO_START = 1
    }
}

