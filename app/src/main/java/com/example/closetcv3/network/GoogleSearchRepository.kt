package com.example.closetcv3.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleSearchRepository {
    private val api: GoogleSearchApi by lazy {
        RetrofitClient.instance.create(GoogleSearchApi::class.java)
    }

    fun searchImages(apiKey: String, cx: String, query: String, onSuccess: (List<String>) -> Unit, onError: (Throwable) -> Unit) {
        val call = api.searchImages(apiKey, cx, query)
        val fullUrl = call.request().url.toString()
        Log.d("GoogleSearchRepository", "Request URL: $fullUrl")
        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val images = response.body()?.items?.map { it.link } ?: emptyList()
                    Log.d("GoogleSearchRepository", "Fetched images: $images")
                    onSuccess(images)
                } else {
                    val errorMsg = "Failed to fetch images: ${response.errorBody()?.string()}"
                    Log.e("GoogleSearchRepository", errorMsg)
                    onError(Exception(errorMsg))
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("GoogleSearchRepository", "Network call failed", t)
                onError(t)
            }
        })
    }
}
