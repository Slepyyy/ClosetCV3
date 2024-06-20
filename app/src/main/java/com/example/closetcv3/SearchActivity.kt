package com.example.closetcv3

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcv3.adapters.ImageAdapter
import com.example.closetcv3.network.GoogleSearchRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchActivity : AppCompatActivity() {
    private val repository = GoogleSearchRepository()
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val boardImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_bot)

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        NavigationUtil.setupBottomNavigationView(this, navView)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchImages(query)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }

        clearIncorrectEntries()
        loadBoardImages()
    }

    private fun clearIncorrectEntries() {
        val sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Check if the value is a String instead of a Set<String> and clear it
        if (sharedPreferences.contains("images")) {
            try {
                sharedPreferences.getStringSet("images", null)
            } catch (e: ClassCastException) {
                // The stored value is a String, not a Set<String>, so clear it
                editor.remove("images").apply()
            }
        }
    }

    private fun loadBoardImages() {
        val sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)
        val images = sharedPreferences.getStringSet("images", null)
        if (images != null) {
            boardImages.clear()
            boardImages.addAll(images)
        }
    }

    private fun searchImages(query: String) {
        val apiKey = "key"
        val cx = "cx"

        repository.searchImages(apiKey, cx, query, ::onImagesFetched, ::onError)
    }

    private fun onImagesFetched(images: List<String>) {
        imageAdapter = ImageAdapter(images, this, ::saveToBoard)
        recyclerView.adapter = imageAdapter
    }

    private fun onError(t: Throwable) {
        Toast.makeText(this, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
    }

    private fun saveToBoard(imageUrl: String) {
        boardImages.add(imageUrl)
        Toast.makeText(this, "Image saved to board", Toast.LENGTH_SHORT).show()
        saveBoardImages()
    }

    private fun saveBoardImages() {
        val sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("images", boardImages.toSet())
        editor.apply()
    }
}
