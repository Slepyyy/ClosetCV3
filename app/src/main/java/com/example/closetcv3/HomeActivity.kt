package com.example.closetcv3

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcv3.adapters.ImageAdapter
import com.example.closetcv3.models.FashionStyle
import com.example.closetcv3.models.fashionStyles
import com.example.closetcv3.network.GoogleSearchRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val fetchedImages = mutableListOf<String>()
    private val repository = GoogleSearchRepository()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // Set 3 columns for the grid

        sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        NavigationUtil.setupBottomNavigationView(this, navView)

        // Get user responses from SharedPreferences
        val userResponses = getUserResponses()
        Log.d("HomeActivity", "Retrieved User Responses: $userResponses")

        // Determine fashion style based on user responses
        val fashionStyle = determineFashionStyle(userResponses)
        Log.d("HomeActivity", "Determined Fashion Style: ${fashionStyle.style}")

        // Fetch images based on determined fashion style
        fetchImagesForStyle(fashionStyle)
    }

    private fun getUserResponses(): ArrayList<String> {
        val sharedPreferences = getSharedPreferences("UserResponses", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("responses", null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            arrayListOf()
        }
    }

    private fun determineFashionStyle(userResponses: List<String>): FashionStyle {
        val styleMapping = mapOf(
            "bright, bold colors" to "Eclectic/Boho",
            "neutrals" to "Minimalist",
            "pastels" to "Soft Girl",
            "dark tones" to "Grunge/Goth",
            "jeans and a comfy t-shirt" to "Casual/Basic",
            "modern stylish" to "Trendy/Streetwear",
            "fancy" to "Chic/Elegant",
            "athletic wear" to "Athleisure",
            "oversized" to "Cozy/Comfy",
            "sneakers" to "Casual/Streetwear",
            "heels" to "Elegant/Chic",
            "sandals" to "Boho/Relaxed",
            "flats" to "Classic/Practical",
            "fancy shoes" to "Glamorous/Elegant",
            "boots" to "Edgy/Functional",
            "comfort" to "Casual/Comfy",
            "fashion trends" to "Trendy/Fashion-forward",
            "versatility" to "Capsule Wardrobe/Classic",
            "unique details" to "Eclectic/Artistic",
            "simple" to "Minimalist/Classic",
            "bold" to "Statement/Boho",
            "practical" to "Functional/Casual",
            "none" to "Natural/Unadorned"
        )

        val lowerCaseResponses = userResponses.map { it.lowercase() }
        val allStyles = lowerCaseResponses.mapNotNull { response ->
            styleMapping.entries.find { response.contains(it.key) }?.value
        }

        val styleCounts = allStyles.groupingBy { it }.eachCount()
        val finalStyle = styleCounts.maxByOrNull { it.value }?.key

        Log.d("determineFashionStyle", "Style Counts: $styleCounts")
        Log.d("determineFashionStyle", "Final Style: $finalStyle")

        return fashionStyles.firstOrNull { it.style == finalStyle } ?: fashionStyles.random()
    }

    private fun fetchImagesForStyle(fashionStyle: FashionStyle) {
        val apiKey = "key"
        val cx = "cx"

        repository.searchImages(apiKey, cx, fashionStyle.searchKeywords, ::onImagesFetched, ::onError)
    }

    private fun onImagesFetched(images: List<String>) {
        fetchedImages.clear()
        fetchedImages.addAll(images)
        imageAdapter = ImageAdapter(fetchedImages, this, ::saveToBoard)
        recyclerView.adapter = imageAdapter
    }

    private fun onError(t: Throwable) {
        Toast.makeText(this, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
    }

    private fun saveToBoard(imageUrl: String) {
        val savedImages = sharedPreferences.getStringSet("images", mutableSetOf())?.toMutableSet()
        savedImages?.add(imageUrl)
        sharedPreferences.edit().putStringSet("images", savedImages).apply()
        Toast.makeText(this, "Image saved to board", Toast.LENGTH_SHORT).show()
    }
}
