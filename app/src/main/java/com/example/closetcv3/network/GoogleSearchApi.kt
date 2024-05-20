package com.example.closetcv3.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleSearchApi {
    @GET(".")
    fun searchImages(
        @Query("key") apiKey: String,
        @Query("cx") cx: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String = "image",
        @Query("num") num: Int = 10
    ): Call<SearchResponse>
}
