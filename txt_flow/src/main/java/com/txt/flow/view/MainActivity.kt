package com.txt.flow.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.txt.flow.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keywordsFlowView.setTextShowSize(12)   // 设置每次随机飞入文字的个数
        keywordsFlowView.shouldScrollFlow(true)  // 设置是否允许滑动屏幕切换文字
        keywordsFlowView.show(keywords, KeywordsFlow.ANIMATION_IN)  // 开始展示

        flowInBTN.setOnClickListener {
            keywordsFlowView.show(
                keywords,
                KeywordsFlow.ANIMATION_IN
            ) // 文字随机飞入
        }

        flowOutBTN.setOnClickListener {
            keywordsFlowView.show(
                keywords,
                KeywordsFlow.ANIMATION_OUT
            ) // 文字随机飞出
        }

        // 设置文字的点击点击监听
        keywordsFlowView.setOnItemClickListener(View.OnClickListener { v ->
            Toast.makeText(
                this@MainActivity,
                (v as TextView).text.toString(),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    companion object {
        @Suppress("SpellCheckingInspection")
        val keywords = arrayOf(
            "微信",
            "QQ",
            "支付宝",
            "天气",
            "小番茄",
            "美团",
            "万能计算器",
            "百度地图",
            "京东",
            "寻常生活",
            "手机淘宝",
            "蜻蜓FM",
            "度小满金融",
            "中国银行",
            "有道词典",
            "下厨房",
            "水果忍者",
            "梦幻小镇",
            "植物大战僵尸2",
            "饥饿鲨",
            "比特小队",
            "2048世界",
            "愤怒的小鸟2",
            "谷歌商店",
            "谷歌翻译",
            "脸书",
            "影梭",
            "V2ray",
            "Play游戏",
            "WhatsApp",
            "推特",
            "Gmail",
            "Tumblr",
            "Instagram",
            "APKPure",
            "Goolge",
            "Tinder",
            "Youtube",
            "Paypal",
            "极简汇率",
            "Snapmod",
            "OneNote",
            "小睡眠",
            "掌阅"
        )
    }
}