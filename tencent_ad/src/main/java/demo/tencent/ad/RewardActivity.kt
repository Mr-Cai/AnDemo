package demo.tencent.ad

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qq.e.ads.rewardvideo.RewardVideoAD
import com.qq.e.ads.rewardvideo.RewardVideoADListener
import com.qq.e.comm.util.AdError
import demo.tencent.ad.O.TAG
import demo.tencent.ad.O.configToolBar
import kotlinx.android.synthetic.main.activity_reward.*

class RewardActivity : AppCompatActivity(), RewardVideoADListener {
    private lateinit var rewardVideoAD: RewardVideoAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)
        configToolBar(toolbar, this)
        loadRewardVideo().loadAD()
        showRewardBTN.setOnClickListener {
            rewardVideoAD.showAD(this)
        }
    }

    private fun loadRewardVideo(isOpenVolume: Boolean = false): RewardVideoAD {
        rewardVideoAD = RewardVideoAD(this, O.rewardID, this, isOpenVolume)
        return rewardVideoAD
    }

    override fun onADExpose() {
        Log.i(TAG, "onADExpose: ")
    }

    override fun onADClick() {
        Log.i(TAG, "onADClick: ")
    }

    override fun onVideoCached() {
        Log.i(TAG, "onVideoCached: ")
    }

    override fun onReward() {
        Log.i(TAG, "onReward: ")
    }

    override fun onADClose() {
        Log.i(TAG, "onADClose: ")
    }

    override fun onADLoad() {
        Toast.makeText(this, "激励视频广告加载成功", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "onADLoad: ")
    }

    override fun onVideoComplete() {
        Log.i(TAG, "onVideoComplete: ")
    }

    override fun onError(error: AdError) {
        Handler(Looper.getMainLooper()).postDelayed({
            loadRewardVideo().loadAD()
        }, 2000)
        Log.i(TAG, "onNoAD: 错误详情: ${error.errorMsg}, 错误码: ${error.errorCode}")
    }

    override fun onADShow() {
        Log.i(TAG, "onADShow: ")
    }
}