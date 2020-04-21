package demo.tencent.ad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
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
    private var mAdData: NativeUnifiedADData? = null

    private val adHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            MSG_INIT_AD -> {
                val ad = msg.obj as NativeUnifiedADData
                Log.i(TAG, "handleMessage:图片宽高: ${ad.pictureWidth} ${ad.pictureHeight}")
                initAd(ad)
                Log.i(
                    TAG,
                    "eCPM: ${ad.ecpm} eCPM等级: ${ad.ecpmLevel} eCPM延迟: ${ad.videoDuration}"
                )
            }
            MSG_VIDEO_START -> {
                img_poster.visibility = View.GONE
                gdt_media_view.visibility = View.VISIBLE
            }
        }
        true
    })

    // 与广告有关的变量，用来显示广告素材的UI
    private lateinit var mAdManager: NativeUnifiedAD
    private var mPlayMute = true
    private var mPreloadVideo = false
    private var mLoadingAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_unified_ad_simple)
        mPlayMute = true
        mAdManager = NativeUnifiedAD(this, O.appID, O.nativeID, this)
        mAdManager.setMinVideoDuration(0)
        mAdManager.setMaxVideoDuration(0)
        // 海外流量
        mAdManager.setVastClassName("com.qq.e.union.demo.adapter.vast.unified.ImaNativeDataAdapter")
        mAdManager.setVideoPlayPolicy(AUTO)
        mAdManager.setVideoADContainerRender(SDK)
        nativeShowBTN.setOnClickListener {
            loadAd(true)
        }
    }

    override fun onADLoaded(ads: List<NativeUnifiedADData>) {
        mLoadingAd = false
        if (ads.isNotEmpty()) {
            val msg = Message.obtain()
            msg.what = MSG_INIT_AD
            mAdData = ads[0]
            msg.obj = mAdData
            adHandler.sendMessage(msg)
        }
    }

    private fun loadAd(preloadVideo: Boolean) {
        mLoadingAd = true
        if (mAdData != null) {
            mAdData?.destroy()
            mAdData = null
        }
        mPreloadVideo = preloadVideo
        mAdManager.loadData(AD_COUNT)
    }

    private fun initAd(ad: NativeUnifiedADData) {
        if (ad.adPatternType == AdPatternType.NATIVE_VIDEO) {
            if (mPreloadVideo) {
                // 如果是视频广告，可以调用preloadVideo预加载视频素材
                Toast.makeText(this, "正在加载视频素材", Toast.LENGTH_SHORT).show()
                ad.preloadVideo(object : VideoPreloadListener {
                    override fun onVideoCached() {
                        Log.i(TAG, "onVideoCached: ")
                        // 视频素材加载完成，此时展示广告不会有进度条。
                        showAd(ad)
                    }

                    override fun onVideoCacheFailed(
                        errorNo: Int,
                        msg: String
                    ) {
                        Log.i(TAG, "onVideoCacheFailed: ")
                    }
                })
            } else {
                showAd(ad)
            }
        } else {
            showAd(ad)
        }
    }

    private fun showAd(ad: NativeUnifiedADData) {
        renderAdUi(ad)
        val clickableViews: MutableList<View?> =
            ArrayList()
        // 所有广告类型，注册mDownloadButton的点击事件
        clickableViews.add(btn_download)
        ad.bindAdToView(this, native_ad_container, null, clickableViews)
        if (ad.adPatternType == AdPatternType.NATIVE_VIDEO) {
            // 视频广告，注册mMediaView的点击事件
            adHandler.sendEmptyMessage(MSG_VIDEO_START)
            val videoOption =
                getVideoOption(intent)
            ad.bindMediaView(
                gdt_media_view,
                videoOption,
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
                        Log.i(TAG, "onVideoLoaded: ")
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
        } else if (ad.adPatternType == AdPatternType.NATIVE_2IMAGE_2TEXT ||
            ad.adPatternType == AdPatternType.NATIVE_1IMAGE_2TEXT
        ) {
            // 双图双文、单图双文：注册mImagePoster的点击事件
            clickableViews.add(img_poster)
        } else {
            // 三小图广告：注册native_3img_ad_container的点击事件
            clickableViews.add(findViewById(R.id.native_3img_ad_container))
        }
        ad.setNativeAdEventListener(object : NativeADEventListener {
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
                    ad
                )
            }
        })
        updateAdAction(btn_download, ad)
    }

    override fun onResume() {
        super.onResume()
        if (mAdData != null) {
            // 必须要在Activity.onResume()时通知到广告数据，以便重置广告恢复状态
            mAdData?.resume()
        }
    }

    private fun renderAdUi(ad: NativeUnifiedADData) {
        val patternType = ad.adPatternType
        val titleTxT = findViewById<TextView>(R.id.text_title)
        val descTxT = findViewById<TextView>(R.id.text_desc)
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
            || patternType == AdPatternType.NATIVE_VIDEO
        ) {
            btn_download.visibility = View.VISIBLE
            img_poster.visibility = View.VISIBLE
            Glide.with(this).load(ad.iconUrl).into(img_logo)
            Glide.with(this).load(ad.iconUrl).into(img_poster)
            titleTxT.text = ad.title
            descTxT.text = ad.desc
        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            titleTxT.text = ad.title
            descTxT.text = ad.desc
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mAdData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mAdData?.destroy()
        }
    }

    override fun onNoAD(error: AdError) {
        Log.i(TAG, "onADError: 错误编号:${error.errorCode}，错误消息:${error.errorMsg}")
        mLoadingAd = false
    }

    companion object {
        private const val MSG_INIT_AD = 0
        private const val MSG_VIDEO_START = 1
        private const val AD_COUNT = 1

        fun getVideoOption(intent: Intent?): VideoOption? {
            if (intent == null) {
                return null
            }
            val videoOption: VideoOption?
            val builder =
                VideoOption.Builder()
            builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS)
            builder.setAutoPlayMuted(true)
            builder.setDetailPageMuted(false)
            builder.setNeedCoverImage(true)
            builder.setNeedProgressBar(true)
            builder.setEnableDetailPage(true)
            builder.setEnableUserControl(false)
            videoOption = builder.build()
            return videoOption
        }

        fun updateAdAction(
            button: Button,
            ad: NativeUnifiedADData
        ) {
            if (!ad.isAppAd) {
                button.text = "浏览"
                return
            }
            when (ad.appStatus) {
                0 -> button.text = "下载"
                1 -> button.text = "启动"
                2 -> button.text = "更新"
                4 -> button.text = ad.progress.toString() + "%"
                8 -> button.text = "安装"
                16 -> button.text = "下载失败，重新下载"
                else -> button.text = "浏览"
            }
        }
    }
}