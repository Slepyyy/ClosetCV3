package com.example.closetcv3

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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

        // Get user responses from intent or savedInstanceState
        val userResponses = intent.getStringArrayListExtra("USER_RESPONSES") ?: emptyList<String>()

        // Determine fashion style based on user responses
        val fashionStyle = determineFashionStyle(userResponses)
        fetchImagesForStyle(fashionStyle)
    }

    private fun determineFashionStyle(userResponses: List<String>): FashionStyle {
        var colorStyle = ""
        var outfitStyle = ""
        var shoeStyle = ""
        var priorityStyle = ""
        var accessoryStyle = ""

        if (userResponses.isNotEmpty()) {
            colorStyle = when (userResponses.getOrNull(0)) {
                "Bright, bold colors" -> "Eclectic/Boho"
                "Neutrals" -> "Minimalist"
                "Pastels" -> "Soft Girl"
                "Dark tones" -> "Grunge/Goth"
                else -> ""
            }
            outfitStyle = when (userResponses.getOrNull(1)) {
                "Jeans and a comfy t-shirt" -> "Casual/Basic"
                "Modern stylish" -> "Trendy/Streetwear"
                "Fancy" -> "Chic/Elegant"
                "Athletic wear" -> "Athleisure"
                "Oversized" -> "Cozy/Comfy"
                else -> ""
            }
            shoeStyle = when (userResponses.getOrNull(2)) {
                "Sneakers" -> "Casual/Streetwear"
                "Heels" -> "Elegant/Chic"
                "Sandals" -> "Boho/Relaxed"
                "Flats" -> "Classic/Practical"
                "Fancy Shoes" -> "Glamorous/Elegant"
                "Boots" -> "Edgy/Functional"
                else -> ""
            }
            priorityStyle = when (userResponses.getOrNull(3)) {
                "Comfort" -> "Casual/Comfy"
                "Fashion trends" -> "Trendy/Fashion-forward"
                "Versatility" -> "Capsule Wardrobe/Classic"
                "Unique details" -> "Eclectic/Artistic"
                else -> ""
            }
            accessoryStyle = when (userResponses.getOrNull(4)) {
                "Simple" -> "Minimalist/Classic"
                "Bold" -> "Statement/Boho"
                "Practical" -> "Functional/Casual"
                "None" -> "Natural/Unadorned"
                else -> ""
            }
        }

        // Determine final fashion style based on majority vote
        val allStyles = listOf(colorStyle, outfitStyle, shoeStyle, priorityStyle, accessoryStyle)
        val styleCounts = allStyles.groupingBy { it }.eachCount()
        val finalStyle = styleCounts.maxByOrNull { it.value }?.key ?: "Casual/Basic"

        // Provide a default fashion style if no match is found
        return fashionStyles.firstOrNull { it.style == finalStyle } ?: FashionStyle(
            style = "Casual/Basic",
            description = "Comfortable and practical, this style focuses on everyday essentials like well-fitted jeans, t-shirts, hoodies, and casual jackets. It’s effortless and laid-back, perfect for day-to-day activities.",
            searchKeywords = "casual fashion"
        )
    }

    private fun fetchImagesForStyle(fashionStyle: FashionStyle) {
        val apiKey = "AIzaSyBulVoJPgELwQmukZ4rSuJk5_MQqy0kB1I"
        val cx = "c159558f5d2334c94"

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
