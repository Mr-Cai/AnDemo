package demo.tencent.ad

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.cfg.VideoOption.VideoADContainerRender.SDK
import com.qq.e.ads.cfg.VideoOption.VideoPlayPolicy.AUTO
import com.qq.e.ads.nativ.*
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import kotlinx.android.synthetic.main.activity_native_unified_ad_simple.*
import java.util.*

class NativeADUnifiedSampleActivity : AppCompatActivity(),
    NativeADUnifiedListener {
    private var adData: NativeUnifiedADData? = null // 广告数据
    private val adHandler: Handler = getHandler()  // 广告接收处理
    private lateinit var nativeUnifiedAD: NativeUnifiedAD // 广告UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_unified_ad_simple)
        loadAd()
        nativeShowBTN.setOnClickListener {
            adData?.destroy()
            nativeUnifiedAD.loadData(1)
        }
    }

    override fun onADLoaded(ads: List<NativeUnifiedADData>) {
        if (ads.isNotEmpty()) {
            val msg = Message.obtain()
            msg.what = MSG_INIT_AD
            adData = ads[0]
            msg.obj = adData
            adHandler.sendMessage(msg)
        }
    }

    private fun loadAd() {
        nativeUnifiedAD = NativeUnifiedAD(this, O.appID, O.nativeID, this)
        nativeUnifiedAD.run {
            setMinVideoDuration(0)
            setMaxVideoDuration(0)
            setVideoPlayPolicy(AUTO)
            setVideoADContainerRender(SDK)
            loadData(1)
        }
    }

    private fun getHandler() = Handler(Handler.Callback { msg ->
        when (msg.what) {
            MSG_INIT_AD -> {
                val ad = msg.obj as NativeUnifiedADData
                initAd(ad)
            }
            MSG_VIDEO_START -> {
                img_poster.visibility = View.GONE
                gdt_media_view.visibility = View.VISIBLE
            }
        }
        true
    })

    private fun initAd(ad: NativeUnifiedADData) {
        if (ad.adPatternType == AdPatternType.NATIVE_VIDEO) {
            Toast.makeText(this, "正在加载视频素材", Toast.LENGTH_SHORT).show()
            ad.preloadVideo(object : VideoPreloadListener {
                override fun onVideoCached() {
                    Log.i(TAG, "onVideoCached: ")
                    showAd(ad, baseContext)
                }

                override fun onVideoCacheFailed(errorNo: Int, msg: String) {
                    Log.i(TAG, "onVideoCacheFailed: ")
                }
            })
            showAd(ad, baseContext)
        }
        showAd(ad, baseContext)
    }

    private fun showAd(adData: NativeUnifiedADData, context: Context) {
        renderAdUi(adData)
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(btn_download)
        adData.bindAdToView(this, native_ad_container, null, clickableViews)
        if (adData.adPatternType == AdPatternType.NATIVE_VIDEO) {
            adHandler.sendEmptyMessage(MSG_VIDEO_START)
            adData.bindMediaView(
                gdt_media_view,
                getVideoOption(),
                object : NativeADMediaListener {
                    override fun onVideoInit() {
                        Log.i(TAG, "onVideoInit: ")
                    }

                    override fun onVideoLoading() {
                        Log.i(TAG, "onVideoLoading: ")
                    }

                    override fun onVideoReady() {
                        Log.i(TAG, "onVideoReady: ")
                    }

                    override fun onVideoLoaded(videoDuration: Int) {
                        Log.i(TAG, "onVideoLoaded: $videoDuration")
                    }

                    override fun onVideoStart() {
                        Log.i(TAG, "onVideoStart: ")
                    }

                    override fun onVideoPause() {
                        Log.i(TAG, "onVideoPause: ")
                    }

                    override fun onVideoResume() {
                        Log.i(TAG, "onVideoResume: ")
                    }

                    override fun onVideoCompleted() {
                        Log.i(TAG, "onVideoCompleted: ")
                    }

                    override fun onVideoError(error: AdError) {
                        Log.i(TAG, "onVideoError: ")
                    }

                    override fun onVideoStop() {
                        Log.i(TAG, "onVideoStop: ")
                    }

                    override fun onVideoClicked() {
                        Log.i(TAG, "onVideoClicked: ")
                    }
                })
        } else if (adData.adPatternType == AdPatternType.NATIVE_2IMAGE_2TEXT ||
            adData.adPatternType == AdPatternType.NATIVE_1IMAGE_2TEXT
        ) {
            clickableViews.add(img_poster)
        }
        adData.setNativeAdEventListener(object : NativeADEventListener {
            override fun onADExposed() {
                Log.i(TAG, "onADExposed: ")
            }

            override fun onADClicked() {
                Log.i(TAG, "onADClicked: ${NativeUnifiedADData.ext["clickUrl"]}")
            }

            override fun onADError(error: AdError) {
                Log.i(TAG, "onADError: 错误编号:${error.errorCode}，错误消息:${error.errorMsg}")
            }

            override fun onADStatusChanged() {
                Log.i(TAG, "onADStatusChanged: ")
                updateAdAction(
                    btn_download,
                    adData,
                    baseContext
                )
            }
        })
        updateAdAction(btn_download, adData, context)
    }

    override fun onResume() {
        super.onResume()
        adData?.resume()
    }

    private fun renderAdUi(ad: NativeUnifiedADData) {
        val patternType = ad.adPatternType
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
            || patternType == AdPatternType.NATIVE_VIDEO
        ) {
            btn_download.visibility = View.VISIBLE
            img_poster.visibility = View.VISIBLE
            Glide.with(this).load(ad.iconUrl).into(img_logo)
            Glide.with(this).load(ad.iconUrl).into(img_poster)
            text_title.text = ad.title
            text_desc.text = ad.desc
        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            text_title.text = ad.title
            text_desc.text = ad.desc
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        adData?.destroy()
    }

    override fun onNoAD(error: AdError) {
        Log.i(TAG, "onADError: 错误编号:${error.errorCode}，错误消息:${error.errorMsg}")
    }

    companion object {
        private const val MSG_INIT_AD = 0
        private const val MSG_VIDEO_START = 1

        fun getVideoOption(): VideoOption? {
            val videoOption: VideoOption?
            val builder = VideoOption.Builder()
            builder.run {
                setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS)
                setAutoPlayMuted(true)
                setDetailPageMuted(false)
                setNeedCoverImage(true)
                setNeedProgressBar(true)
                setEnableDetailPage(true)
                setEnableUserControl(false)
                videoOption = build()
            }
            return videoOption
        }

        @SuppressLint("SetTextI18n")
        fun updateAdAction(button: Button, adData: NativeUnifiedADData, ctx: Context) {
            when {
                !adData.isAppAd -> {
                    button.text = ctx.getText(R.string.views)
                    return
                }
                else -> when (adData.appStatus) {
                    0 -> button.text = ctx.getText(R.string.download)
                    1 -> button.text = ctx.getText(R.string.start)
                    2 -> button.text = ctx.getText(R.string.update)
                    4 -> button.text = "${adData.progress}%"
                    8 -> button.text = ctx.getText(R.string.install)
                    16 -> button.text = ctx.getText(R.string.retry)
                    else -> button.text = ctx.getText(R.string.retry)
                }
            }
        }
    }
}