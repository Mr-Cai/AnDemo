package com.txt.flow.data

import retrofit2.http.GET
import retrofit2.http.Query

interface KeyWordService {
    @GET("wordList.json")
    fun getWordListAsync(@Query("uid") uid: Int, @Query("typeId") typeId: Int)

    @GET("typeList.json")
    fun getTypeListAsync(@Query("uid") uid: Int)
}