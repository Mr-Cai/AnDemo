package demo.tencent.ad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.*
import com.qq.e.ads.nativ.widget.NativeAdContainer
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.managers.status.SDKStatus
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_render_recycler_native.*
import kotlinx.android.synthetic.main.item_data.view.*
import kotlinx.android.synthetic.main.item_native_render.view.*
import java.util.*

class NativeRecyclerActivity : AppCompatActivity(),
    NativeADUnifiedListener {
    private lateinit var nativeUnifiedAD: NativeUnifiedAD
    private var adDataList: MutableList<NativeUnifiedADData>? = ArrayList()
    private lateinit var adapter: CustomAdapter
    private val adHandler by lazy { getHandler() }
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_recycler_native)
        configToolBar(toolbar, this)
        initAD()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.native_ad_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nativeRender -> {
                startActivity(
                    Intent(
                        this,
                        NativeADActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAD() {
        nativeUnifiedAD = NativeUnifiedAD(this, O.appID, O.nativeID, this)
        nativeUnifiedAD.setMinVideoDuration(0)
        nativeUnifiedAD.setMaxVideoDuration(0)
        nativeUnifiedAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
        nativeUnifiedAD.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK)
        nativeUnifiedAD.loadData(AD_COUNT)
    }

    private fun initView() {
        nativeRecycler.layoutManager = LinearLayoutManager(this)
        val list = ArrayList<Any>()
        for (i in 0..9) {
            list.add(NormalItem("No.$i Init Data"))
        }
        adapter = CustomAdapter(this, list)
        nativeRecycler.adapter = adapter
        nativeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!isLoading && newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !recyclerView.canScrollVertically(1)
                ) {
                    isLoading = true
                    nativeUnifiedAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
                    nativeUnifiedAD.setVideoADContainerRender(VideoOption.VideoADContainerRender.DEV)
                    nativeUnifiedAD.loadData(AD_COUNT)
                }
            }
        })

    }

    override fun onADLoaded(ads: List<NativeUnifiedADData>) {
        isLoading = false
        if (adDataList != null) {
            adDataList!!.addAll(ads)
            val msg = adHandler.obtainMessage(
                MSG_REFRESH_LIST,
                ads
            )
            adHandler.sendMessage(msg)
        }
    }

    override fun onNoAD(error: AdError) {
        isLoading = false
        Log.i(TAG, "onADError: 错误编号: ${error.errorCode}, 错误消息: ${error.errorMsg}")
    }

    override fun onResume() {
        super.onResume()
        if (adDataList != null) {
            for (ad in adDataList!!) {
                ad.resume()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adDataList != null) {
            for (ad in adDataList!!) {
                ad.destroy()
            }
        }
        adDataList = null
    }

    private fun getHandler(): Handler {
        return Handler(Handler.Callback { msg ->
            when (msg.what) {
                MSG_REFRESH_LIST -> {
                    val count = adapter.itemCount
                    for (i in 0 until ITEM_COUNT) {
                        adapter.addNormalItem(
                            NormalItem("标题$i")
                        )
                    }
                    val ads =
                        msg.obj as List<NativeUnifiedADData>
                    if (ads.isNotEmpty()) {
                        var i = 0
                        while (i < ads.size) {
                            adapter.addAdToPosition(
                                ads[i],
                                count + i * AD_DISTANCE + FIRST_AD_POSITION
                            )
                            Log.i(
                                TAG,
                                "$i: eCPM = " + ads[i]
                                    .ecpm + " , eCPMLevel = " + ads[i]
                                    .ecpmLevel + " , videoDuration = " + ads[i]
                                    .videoDuration
                            )
                            i++
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            true
        })
    }

    internal inner class CustomAdapter(
        private val mContext: Context,
        private val dataList: MutableList<Any>
    ) : RecyclerView.Adapter<CustomHolder>() {
        private val adSet = TreeSet<Any?>()
        fun addNormalItem(item: NormalItem) {
            dataList.add(item)
        }

        fun addAdToPosition(
            nativeUnifiedADData: NativeUnifiedADData,
            position: Int
        ) {
            if (position >= 0 && position < dataList.size) {
                dataList.add(position, nativeUnifiedADData)
                adSet.add(position)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                adSet.contains(position) -> {
                    TYPE_AD
                }
                adSet.contains(position + 1) -> {
                    TYPE_SHOW_SDK_VERSION
                }
                else -> {
                    TYPE_DATA
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CustomHolder {
            val view = when (viewType) {
                TYPE_AD -> LayoutInflater.from(mContext)
                    .inflate(R.layout.item_native_render, parent, false)
                TYPE_DATA, TYPE_SHOW_SDK_VERSION -> LayoutInflater.from(mContext)
                    .inflate(R.layout.item_data, parent, false)
                else -> null
            }
            return CustomHolder(view!!, viewType)
        }

        override fun onBindViewHolder(
            holder: CustomHolder,
            position: Int
        ) {
            val itemView = holder.itemView
            when (getItemViewType(position)) {
                TYPE_AD -> {
                    val ad = dataList[position] as NativeUnifiedADData
                    holder.title.text = ad.title
                    holder.desc!!.text = ad.desc
                    Glide.with(holder.itemView.context).load(ad.iconUrl).into(holder.logo!!)
                    Glide.with(holder.itemView.context).load(ad.imgUrl).into(holder.poster!!)
                    // 视频广告
                    if (ad.adPatternType == 2) {
                        holder.poster!!.visibility = View.INVISIBLE
                        holder.mediaView!!.visibility = View.VISIBLE
                    } else {
                        holder.poster!!.visibility = View.VISIBLE
                        holder.mediaView!!.visibility = View.INVISIBLE
                    }
                    ad.bindAdToView(
                        this@NativeRecyclerActivity, holder.container, null,
                        mutableListOf(holder.poster, holder.topBarLayout)
                    )
                    setAdListener(holder, ad)
                }
                TYPE_DATA -> itemView.titleTxT.text = (dataList[position] as NormalItem).title
                TYPE_SHOW_SDK_VERSION -> itemView.titleTxT!!.text =
                    "腾讯广告SDK版本:\t${SDKStatus.getIntegrationSDKVersion()}"
            }
        }

        private fun setAdListener(
            holder: CustomHolder,
            ad: NativeUnifiedADData
        ) {
            ad.setNativeAdEventListener(object : NativeADEventListener {
                override fun onADExposed() {
                    Log.i(TAG, "onADExposed: ")
                }

                override fun onADClicked() {
                    Log.i(TAG, "onADClicked: ")
                }

                override fun onADError(error: AdError) {
                    Log.i(TAG, "onADError: 错误编号: ${error.errorCode}, 错误消息: ${error.errorMsg}")
                }

                override fun onADStatusChanged() {
                    Log.i(TAG, "onADStatusChanged: ")
                }
            })
            // 视频广告
            if (ad.adPatternType == AdPatternType.NATIVE_VIDEO) {
                ad.bindMediaView(
                    holder.mediaView,
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
                            Log.i(
                                TAG,
                                "onADError: 错误编号: ${error.errorCode}, 错误消息: ${error.errorMsg}"
                            )
                        }

                        override fun onVideoStop() {
                            Log.i(TAG, "onVideoStop: ")
                        }

                        override fun onVideoClicked() {
                            Log.i(TAG, "onVideoClicked: ")
                        }
                    })
            }
        }

        override fun getItemCount() = dataList.size

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

    }


    internal class CustomHolder(itemView: View, adType: Int) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var title: TextView
        var mediaView: MediaView? = null
        var desc: TextView? = null
        var logo: ImageView? = null
        var poster: ImageView? = null
        var container: NativeAdContainer? = null
        lateinit var topBarLayout: LinearLayout

        init {
            when (adType) {
                TYPE_AD -> {
                    mediaView = itemView.adMediaView
                    logo = itemView.logoPic
                    poster = itemView.adPoster
                    desc = itemView.descTxTAD
                    container = itemView.adNativeContainer
                    title = itemView.titleTxTAD
                    topBarLayout = itemView.topBarLayout
                }
                TYPE_DATA, TYPE_SHOW_SDK_VERSION -> title =
                    itemView.titleTxT
            }
        }
    }

    inner class NormalItem(val title: String)

    companion object {
        private const val AD_COUNT = 3
        private const val ITEM_COUNT = 30
        private const val FIRST_AD_POSITION = 5
        private const val AD_DISTANCE = 10
        private const val MSG_REFRESH_LIST = 1
        private const val TYPE_DATA = 0
        private const val TYPE_AD = 1
        private const val TYPE_SHOW_SDK_VERSION = 2
    }
}