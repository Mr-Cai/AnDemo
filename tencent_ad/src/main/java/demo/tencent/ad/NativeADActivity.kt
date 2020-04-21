package demo.tencent.ad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Handler.Callback
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.NativeADUnifiedListener
import com.qq.e.ads.nativ.NativeUnifiedAD
import com.qq.e.ads.nativ.NativeUnifiedADData
import com.qq.e.ads.nativ.VideoPreloadListener
import com.qq.e.comm.constants.AdPatternType
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_native_a_d.*
import kotlinx.android.synthetic.main.item_data.view.*

class NativeADActivity : AppCompatActivity(), NativeADUnifiedListener {
    private val mockDataList = ArrayList<NormalItem>()
    private lateinit var nativeUnifiedAD: NativeUnifiedAD
    private lateinit var customAdapter: CustomAdapter

    private lateinit var adData: NativeUnifiedADData

    private val adHandler = Handler(Callback { msg ->
        when (msg.what) {
            MSG_INIT_AD -> {
                val adData = msg.obj as NativeUnifiedADData
                initAd(adData)
            }
            MSG_VIDEO_START -> {
                adPostPic.visibility = View.GONE
                mediaView.visibility = View.VISIBLE
            }
        }
        true
    })

    private var isShowNativeAD: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_a_d)
        configToolBar(toolbar, this, "自渲染消息流")
        initData()
        nativeUnifiedAD = NativeUnifiedAD(this, O.appID, O.nativeID, this)
        nativeUnifiedAD.run {
            setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO)
            setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK)
            loadData(6)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.native_ad_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nativeRender -> {
                startActivity(Intent(this, NativeADUnifiedSampleActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adHandler.removeCallbacksAndMessages(null)
    }

    private fun initData() {
        nativeRecycler.layoutManager = LinearLayoutManager(this)
        nativeRecycler.setHasFixedSize(true)
        for (i in 0 until 30) mockDataList.add(NormalItem("标题 $i"))
        customAdapter = CustomAdapter(mockDataList)
        nativeRecycler.adapter = customAdapter
    }

    private fun initAd(adData: NativeUnifiedADData) {
        if (adData.adPatternType == AdPatternType.NATIVE_VIDEO) {
            adData.preloadVideo(object : VideoPreloadListener {
                override fun onVideoCached() {
                    showAd(adData)
                }

                override fun onVideoCacheFailed(errorNo: Int, msg: String) {
                    Log.i(TAG, "onVideoCacheFailed: 错误编号:$errorNo, 错误消息:$msg")
                }
            })
            showAd(adData)
        }
    }

    private fun showAd(adData: NativeUnifiedADData) {
        renderADUi(adData)

        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.addAll(
            arrayListOf(
                adPostPic,
                installBTN
            )
        )
        adData.bindAdToView(this, nativeADContainer, null, clickableViews)

    }

    private fun renderADUi(adData: NativeUnifiedADData) {
        Log.i(TAG, "renderADUi: ${adData.imgUrl}")
        Glide.with(this).load(adData.imgUrl).into(adPostPic)
        Glide.with(this).load(adData.iconUrl).into(adLogo)
    }

    override fun onADLoaded(dataList: MutableList<NativeUnifiedADData>) {
        Toast.makeText(this, "自渲染广告加载成功", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onADLoaded: ")
        val msg = Message.obtain()
        msg.what = MSG_INIT_AD
        adData = dataList[0]
        msg.obj = adData
        adHandler.handleMessage(msg)
    }

    override fun onNoAD(error: AdError) {
        Handler().postDelayed({
            nativeUnifiedAD.loadData(6)
        }, 2000)
        Log.i(TAG, "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

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

    companion object {
        private const val TYPE_DATA: Int = 0
        private const val TYPE_AD: Int = 1
        private const val TYPE_VERSION: Int = 2
        private const val MSG_INIT_AD = 0
        private const val MSG_VIDEO_START = 1
    }
}

