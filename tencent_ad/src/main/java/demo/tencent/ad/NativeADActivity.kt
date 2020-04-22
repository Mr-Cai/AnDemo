package demo.tencent.ad

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.*
import com.qq.e.ads.nativ.widget.NativeAdContainer
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_native_a_d.*
import kotlinx.android.synthetic.main.item_data.view.*

class NativeADActivity : AppCompatActivity(), NativeADUnifiedListener {
    private val mockDataList = ArrayList<NormalItem>()
    private lateinit var customAdapter: CustomAdapter
    private lateinit var renderADAdapter: RenderADAdapter

    private var adData: NativeUnifiedADData? = null // 广告数据
    private val adHandler: Handler = getHandler()  // 广告接收处理
    private lateinit var nativeUnifiedAD: NativeUnifiedAD  // 广告UI

    private var isShowNativeRenderAD: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_a_d)
        configToolBar(toolbar, this, "自渲染消息流")
        initData()
        loadAd()

        nativeShowBTN.setOnClickListener {
            adData?.destroy()
            nativeUnifiedAD.loadData(1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.native_ad_menu, menu)
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
                isShowNativeRenderAD = !isShowNativeRenderAD
                if (isShowNativeRenderAD) {
                    nativeRecycler.visibility = View.GONE
                    nativeContainer.visibility = View.VISIBLE
                } else {
                    adData?.destroy()
                    nativeRecycler.visibility = View.VISIBLE
                    nativeContainer.visibility = View.GONE
                }
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

    private fun initData() {
        nativeRecycler.layoutManager = LinearLayoutManager(this)
        nativeRecycler.setHasFixedSize(true)
        for (i in 0 until 30) mockDataList.add(NormalItem("标题 $i"))
        customAdapter = CustomAdapter(mockDataList)
        nativeRecycler.adapter = customAdapter
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

    inner class CustomAdapter(private val dataList: MutableList<NormalItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
            )

        override fun getItemCount() = dataList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemView = holder.itemView
//            val context = itemView.context
            itemView.titleTxT.text = dataList[position].title
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    data class NormalItem(val title: String)

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
                    showAd(ad, baseContext)
                }

                override fun onVideoCacheFailed(errorNo: Int, msg: String) {
                    Log.i(O.TAG, "onVideoCacheFailed: ")
                }
            })
            showAd(ad, baseContext)
        }
        showAd(ad, baseContext)
    }

    private fun showAd(adData: NativeUnifiedADData, context: Context) {
        renderAdUi(adData)
        val clickableViews: MutableList<View> = java.util.ArrayList()
        clickableViews.add(btn_download)
        adData.bindAdToView(this, nativeContainer, null, clickableViews)
        if (adData.adPatternType == AdPatternType.NATIVE_VIDEO) {
            adHandler.sendEmptyMessage(MSG_VIDEO_START)
            adData.bindMediaView(
                gdt_media_view,
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
            clickableViews.add(img_poster)
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
                updateAdAction(
                    btn_download,
                    adData,
                    baseContext
                )
            }
        })
        updateAdAction(btn_download, adData, context)
    }

    private fun renderAdUi(ad: NativeUnifiedADData) {
        val patternType = ad.adPatternType
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
            || patternType == AdPatternType.NATIVE_VIDEO
        ) {
            btn_download.visibility = View.VISIBLE
            img_poster.visibility = View.VISIBLE
            Glide.with(this).load(ad.iconUrl).into(img_logo)
            Glide.with(this).load(ad.imgUrl).into(img_poster)
            text_title.text = ad.title
            text_desc.text = ad.desc
        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            text_title.text = ad.title
            text_desc.text = ad.desc
        }
    }

    private fun getVideoOption(): VideoOption? {
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

    companion object {
        private const val MSG_INIT_AD = 0
        private const val MSG_VIDEO_START = 1
    }

    inner class RenderADAdapter(private val list: MutableList<NativeAdContainer>) :
        RecyclerView.Adapter<RenderADAdapter.RenderADViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RenderADViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_native_render,
                        parent,
                        false
                    )
            )

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: RenderADViewHolder, position: Int) {

        }

        inner class RenderADViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}

