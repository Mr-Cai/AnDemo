package demo.tencent.ad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.*
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.managers.status.SDKStatus
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_render_recycler_native.*
import kotlinx.android.synthetic.main.item_data.view.*
import kotlinx.android.synthetic.main.item_native_render.view.*
import java.util.*
import kotlin.collections.ArrayList

class NativeRecyclerActivity : AppCompatActivity(),
    NativeADUnifiedListener {
    private lateinit var nativeUnifiedAD: NativeUnifiedAD
    private lateinit var adDataList: MutableList<NativeUnifiedADData>
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
        menuInflater.inflate(R.menu.native_ad_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.nativeRender -> {
            startActivity(Intent(this, NativeADActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun initAD() {
        nativeUnifiedAD = NativeUnifiedAD(this, O.appID, O.nativeID, this)
        nativeUnifiedAD.run {
            setMinVideoDuration(0)
            setMaxVideoDuration(0)
            setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
            setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK)
            loadData(AD_COUNT)
        }
    }

    private fun initView() {
        adDataList = ArrayList()
        adapter = CustomAdapter(this, ArrayList())
        nativeRecycler.layoutManager = LinearLayoutManager(this)
        nativeRecycler.adapter = adapter
        nativeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!isLoading && newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !recyclerView.canScrollVertically(1)
                ) {
                    isLoading = true
                    nativeUnifiedAD.run {
                        setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
                        setVideoADContainerRender(VideoOption.VideoADContainerRender.DEV)
                        loadData(AD_COUNT)
                    }
                }
            }
        })
    }

    override fun onADLoaded(ads: List<NativeUnifiedADData>) {
        isLoading = false
        adDataList.addAll(ads)
        val msg = adHandler.obtainMessage(
            MSG_REFRESH_LIST,
            ads
        )
        adHandler.sendMessage(msg)
    }

    override fun onNoAD(error: AdError) {
        isLoading = false
        Log.i(TAG, "onADError: 错误编号: ${error.errorCode}, 错误消息: ${error.errorMsg}")
    }

    override fun onResume() {
        for (adData in adDataList) adData.resume()
        super.onResume()
    }

    override fun onDestroy() {
        for (adData in adDataList) adData.destroy()
        adDataList.clear()
        super.onDestroy()
    }

    private fun getHandler(): Handler {
        return Handler(Handler.Callback { msg ->
            when (msg.what) {
                MSG_REFRESH_LIST -> {
                    val count = adapter.itemCount
                    for (i in 0 until ITEM_COUNT) {
                        adapter.addNormalItem(NormalItem("标题$i"))
                    }
                    val ads = msg.obj as List<*>
                    when {
                        ads.isNotEmpty() -> {
                            for (i in ads.indices) {
                                val adData = ads[i] as NativeUnifiedADData
                                adapter.addAdToPosition(
                                    adData,
                                    count + i * AD_DISTANCE + FIRST_AD_POSITION
                                )
                                Log.i(
                                    TAG,
                                    "$i: eCPM: ${adData.ecpm}\n" + "eCPMLevel: ${adData.ecpmLevel} " +
                                            "videoDuration: ${adData.videoDuration} "
                                )
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            true
        })
    }

    inner class CustomAdapter(
        private val mContext: Context,
        private val dataList: MutableList<Any>
    ) : RecyclerView.Adapter<CustomHolder>() {
        private val adSet = TreeSet<Any>()

        fun addNormalItem(item: NormalItem) {
            dataList.add(item)
        }

        fun addAdToPosition(adData: NativeUnifiedADData, position: Int) {
            if (position >= 0 && position < dataList.size) {
                dataList.add(position, adData)
                adSet.add(position)
            }
        }

        override fun getItemViewType(position: Int) = when {
            adSet.contains(position) -> TYPE_AD
            adSet.contains(position + 1) -> TYPE_SHOW_SDK_VERSION
            else -> TYPE_DATA
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomHolder {
            val view = when (viewType) {
                TYPE_AD -> LayoutInflater.from(mContext)
                    .inflate(R.layout.item_native_render, parent, false)
                TYPE_DATA, TYPE_SHOW_SDK_VERSION -> LayoutInflater.from(mContext)
                    .inflate(R.layout.item_data, parent, false)
                else -> null
            }
            return CustomHolder(view!!)
        }

        override fun onBindViewHolder(holder: CustomHolder, position: Int) {
            val itemView = holder.itemView
            when (getItemViewType(position)) {
                TYPE_AD -> {
                    val adData = dataList[position] as NativeUnifiedADData
                    itemView.titleTxTAD.text = adData.title
                    itemView.descTxTAD.text = adData.desc
                    Glide.with(holder.itemView.context).load(adData.iconUrl).into(itemView.logoPic)
                    Glide.with(holder.itemView.context).load(adData.imgUrl).into(itemView.adPoster)
                    // 视频广告
                    when (adData.adPatternType) {
                        2 -> {
                            itemView.adPoster.visibility = View.INVISIBLE
                            itemView.adMediaView.visibility = View.VISIBLE
                        }
                        else -> {
                            itemView.adPoster.visibility = View.VISIBLE
                            itemView.adMediaView.visibility = View.INVISIBLE
                        }
                    }
                    adData.bindAdToView(
                        this@NativeRecyclerActivity, itemView.adNativeContainer, null,
                        mutableListOf(itemView.adPoster, itemView.topBarLayout)
                    )
                    setAdListener(itemView, adData)
                }
                TYPE_DATA -> itemView.titleTxT.text = (dataList[position] as NormalItem).title
                TYPE_SHOW_SDK_VERSION -> itemView.titleTxT!!.text =
                    String.format(
                        getString(R.string.versions),
                        SDKStatus.getIntegrationSDKVersion()
                    )
            }
        }

        private fun setAdListener(itemView: View, adData: NativeUnifiedADData) {
            adData.setNativeAdEventListener(object : NativeADEventListener {
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
            when (adData.adPatternType) {
                AdPatternType.NATIVE_VIDEO -> {
                    adData.bindMediaView(
                        itemView.adMediaView,
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

    class CustomHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    data class NormalItem(val title: String)

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