package com.example.closetcv3

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.closetcv3.databinding.ActivityCameraBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                dispatchTakePictureIntent()
            }
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        NavigationUtil.setupBottomNavigationView(this, navView)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            saveImageToExternalStorage(imageBitmap)
        }
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, "JPEG_$timeStamp.jpg")
        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            askToSaveImageToBoard(imageFile.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askToSaveImageToBoard(imagePath: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save Image")
        builder.setMessage("Do you want to save this image to the board?")
        builder.setPositiveButton("Yes") { _, _ ->
            saveImageToBoard(imagePath)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun saveImageToBoard(imagePath: String) {
        val sharedPreferences = getSharedPreferences("BoardImages", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = sharedPreferences.getString("images", null)
        val type = object : TypeToken<MutableList<String>>() {}.type
        val images: MutableList<String> = if (json != null) Gson().fromJson(json, type) else mutableListOf()
        images.add(imagePath)
        editor.putString("images", Gson().toJson(images))
        editor.apply()

        Toast.makeText(this, "Image saved to board", Toast.LENGTH_SHORT).show()
    }
}
