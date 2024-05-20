package com.example.closetcv3

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetcv3.adapters.BoardImageAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class BoardActivity : AppCompatActivity() {
    private lateinit var boardRecyclerView: RecyclerView
    private lateinit var boardImageAdapter: BoardImageAdapter
    private val boardImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        boardRecyclerView = findViewById(R.id.boardRecyclerView)
        boardRecyclerView.layoutManager = GridLayoutManager(this, 3) // Set 3 columns for the grid

        boardImageAdapter = BoardImageAdapter(boardImages) { imageUrl ->
            removeImageFromBoard(imageUrl)
        }
        boardRecyclerView.adapter = boardImageAdapter

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        NavigationUtil.setupBottomNavigationView(this, navView)

        // Load saved images from SharedPreferences
        loadBoardImages()
    }

    private fun loadBoardImages() {
        val sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)
        val images = sharedPreferences.getStringSet("images", null)
        if (images != null) {
            boardImages.clear()
            boardImages.addAll(images)
            boardImageAdapter.notifyDataSetChanged()
        }
    }

    private fun saveBoardImages() {
        val sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("images", boardImages.toSet())
        editor.apply()
    }

    fun addImageToBoard(imageUrl: String) {
        boardImages.add(imageUrl)
        boardImageAdapter.notifyItemInserted(boardImages.size - 1)
        saveBoardImages()
    }

    private fun removeImageFromBoard(imageUrl: String) {
        boardImages.remove(imageUrl)
        boardImageAdapter.notifyDataSetChanged()
        saveBoardImages()
    }

    override fun onPause() {
        super.onPause()
        saveBoardImages()
    }
}
