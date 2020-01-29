package com.txt.flow.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KeyWordResponse(
    val code: String, // 1000
    @Suppress("SpellCheckingInspection")
    @SerializedName("datas")
    val dataSet: List<Data>,
    val msg: String // success
) : Parcelable {
    @Parcelize
    data class Data(
        val level: String, // 热搜排名
        val name: String, // 热搜词名
        val num: Int, // 序号
        val trend: String // 趋势
    ) : Parcelable
}