package demo.tencent.ad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qq.e.ads.nativ.express2.*
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_native_express_list.*
import kotlinx.android.synthetic.main.item_data.view.*
import kotlinx.android.synthetic.main.item_express_ad.view.*
import java.util.*

class NativeExpressListActivity : AppCompatActivity(), NativeExpressAD2.AdLoadListener {
    private val normalDataList = ArrayList<NormalItem>()
    private val mAdViewPositionMap = HashMap<NativeExpressADData2, Int>()
    private val expressADDataSet = ArrayList<NativeExpressADData2>()
    private var isLoading = true

    private var adapter: CustomAdapter? = null
    private var nativeExpressAD: NativeExpressAD2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_express_list)
        configToolBar(toolbar, this)

        nativeExpressRecycler.setHasFixedSize(true)
        nativeExpressRecycler.layoutManager = LinearLayoutManager(this)
        nativeExpressRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 滚动到底再次加载广告
                if (!isLoading && newState == RecyclerView.SCROLL_STATE_IDLE
                    && !recyclerView.canScrollVertically(1)
                ) {
                    isLoading = true
                    nativeExpressAD!!.loadAd(AD_COUNT)
                }
            }
        })
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.native_express_menu, menu)
        menu.findItem(R.id.nativeExpressList).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nativeExpressAD -> {
                startActivity(
                    Intent(
                        this,
                        NativeExpressActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (adData in expressADDataSet) {
            adData.destroy()
        }
    }

    private fun initData() {
        adapter = CustomAdapter(normalDataList.toMutableList())
        nativeExpressRecycler!!.adapter = adapter

        // 初始化广告配置
        nativeExpressAD = NativeExpressAD2(this, O.nativeExpressID, this)
        nativeExpressAD!!.setAdSize(420, 0) // 单位 dp
        nativeExpressAD!!.setVideoOption2(
            VideoOption2.Builder()
                .setAutoPlayPolicy(VideoOption2.AutoPlayPolicy.ALWAYS)
                .setAutoPlayMuted(true)     // 自动播放时是否为静音
                .setDetailPageMuted(false)  // 视频详情页是否为静音
                .setMaxVideoDuration(0)     // 设置广告最大时长 5s<=time<=60s (0:无限制)
                .setMinVideoDuration(0)     // 设置广告最小时长 5s<=time<=60s (0:无限制)
                .build()
        )
        nativeExpressAD!!.loadAd(AD_COUNT)
    }

    override fun onLoadSuccess(adDataList: List<NativeExpressADData2>) {
        loadingTxT.visibility = View.GONE
        isLoading = false
        val itemCount: Int = adapter?.itemCount ?: 0

        Log.i(TAG, "onLoadSuccess: adSize: ${expressADDataSet.size}")
        for (i in 0 until ITEMS_COUNT) {
            normalDataList.add(
                NormalItem(
                    "No." + (itemCount + i - expressADDataSet.size) + " Normal Data"
                )
            )
            adapter?.notifyItemInserted(itemCount + i)
        }

        val iterator = adDataList.iterator()
        processADData(itemCount, iterator, 0)
        expressADDataSet.addAll(adDataList)
    }

    // 遍历渲染广告数据
    private fun processADData(
        startPosition: Int,
        iterator: Iterator<NativeExpressADData2>,
        i: Int
    ) {
        if (iterator.hasNext()) {
            val data = iterator.next()
            val position = startPosition + FIRST_AD_POSITION + ITEMS_PER_AD * i + 1
            if (position < normalDataList.size) {
                data.setAdEventListener(object : AdEventListener {
                    override fun onClick() {
                        Log.i(TAG, "onClick, position:$position")
                    }

                    override fun onExposed() {
                        Log.i(TAG, "onImpression, position:$position")
                    }

                    override fun onRenderSuccess() {
                        Log.i(TAG, "onRenderSuccess, position:$position")
                        mAdViewPositionMap[data] = position
                        adapter!!.addAdToPosition(position, data)
                        adapter!!.notifyItemInserted(position)
                        // 当前广告渲染成功，开始渲染下一条广告
                        processADData(startPosition, iterator, i + 1)
                    }

                    override fun onRenderFail() {
                        Log.i(TAG, "onRenderFail, position:$position")
                        // 当前广告渲染失败，开始渲染下一条广告
                        processADData(startPosition, iterator, i + 1)
                    }

                    override fun onAdClosed() {
                        data.destroy()
                        Log.i(TAG, "onAdClosed, position:$position")
                        if (adapter != null) {
                            adapter!!.removeADView(mAdViewPositionMap[data]!!)
                        }
                    }
                })
                data.setMediaListener(object : MediaEventListener {
                    override fun onVideoCache() {
                        Log.i(TAG, "onVideoCache, position:$position")
                    }

                    override fun onVideoStart() {
                        Log.i(TAG, "onVideoStart, position:$position")
                    }

                    override fun onVideoResume() {
                        Log.i(TAG, "onVideoResume, position:$position")
                    }

                    override fun onVideoPause() {
                        Log.i(TAG, "onVideoPause, position:$position")
                    }

                    override fun onVideoComplete() {
                        Log.i(TAG, "onVideoComplete, position:$position")
                    }

                    override fun onVideoError() {
                        Log.i(TAG, "onVideoError, position:$position")
                    }
                })
                Log.i(
                    TAG, data.toString() + "  eCPM level = " + data.ecpmLevel +
                            "  Video duration: " + data.videoDuration
                )
                data.render()  // 开始渲染广告
            }
        }
    }

    override fun onNoAD(error: AdError) {
        isLoading = false
        Log.i(
            TAG, String.format(
                "onNoAD: error code : %d, error msg %s",
                error.errorCode,
                error.errorMsg
            )
        )
    }

    class NormalItem(var title: String)

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CustomAdapter(private val dateList: MutableList<Any>) :
        RecyclerView.Adapter<CustomViewHolder?>() {

        // 把返回的 NativeExpressAD2Data 添加到数据集里面去
        fun addAdToPosition(position: Int, nativeExpressADData2: NativeExpressADData2?) {
            if (position >= 0 && position < dateList.size && nativeExpressADData2 != null) {
                dateList.add(position, nativeExpressADData2)
            }
        }

        // 移除 NativeExpressAD2Data 的时候是一条一条移除的
        fun removeADView(position: Int) {
            dateList.removeAt(position)
            adapter!!.notifyItemRemoved(position)
            adapter!!.notifyItemRangeChanged(0, dateList.size - 1)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {
            val layoutId: Int =
                if (viewType == TYPE_AD) R.layout.item_express_ad else R.layout.item_data
            val view = LayoutInflater.from(viewGroup.context).inflate(layoutId, null)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val viewType = getItemViewType(position)
            val itemView = holder.itemView
            if (TYPE_AD == viewType) {
                val adData = dateList[position] as NativeExpressADData2
                mAdViewPositionMap[adData] = position // 广告在列表中的位置是可以被更新的
                val adView = adData.adView
                if (itemView.nativeADContainer.childCount > 0
                    && itemView.nativeADContainer.getChildAt(0) == adView
                ) {
                    return
                }
                if (itemView.nativeADContainer.childCount > 0) {
                    itemView.nativeADContainer.removeAllViews()
                }
                if (adView != null && adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }
                itemView.nativeADContainer.addView(adView)
            } else {
                itemView.titleTxT.text = (dateList[position] as NormalItem).title
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (dateList[position] is NativeExpressADData2) {
                TYPE_AD
            } else {
                TYPE_DATA
            }
        }

        override fun getItemCount(): Int = dateList.size
    }

    companion object {
        const val ITEMS_COUNT = 50
        const val AD_COUNT = 5  // 加载广告的条数，取值范围为[1, 10]
        var FIRST_AD_POSITION = 1   // 第一条广告的位置
        var ITEMS_PER_AD = 5    // 每间隔5个条目插入一条广告

        private const val TYPE_DATA = 0 // 模拟数据
        private const val TYPE_AD = 1  // 广告组件
    }
}