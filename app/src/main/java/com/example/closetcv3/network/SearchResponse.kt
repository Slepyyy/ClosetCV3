package com.example.closetcv3.network


import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items")
    val items: List<SearchItem>
)

