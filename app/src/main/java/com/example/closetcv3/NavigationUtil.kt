package com.example.closetcv3

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationUtil {

    fun setupBottomNavigationView(activity: Activity, navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (activity !is HomeActivity) {
                        activity.startActivity(Intent(activity, HomeActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_search -> {
                    if (activity !is SearchActivity) {
                        activity.startActivity(Intent(activity, SearchActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_board -> {
                    if (activity !is BoardActivity) {
                        activity.startActivity(Intent(activity, BoardActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_user -> {
                    if (activity !is ChatBot) {
                        activity.startActivity(Intent(activity, ChatBot::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_camera -> {
                    if (activity !is CameraActivity) {
                        activity.startActivity(Intent(activity, CameraActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                else -> false
            }
        }

        // Highlight the current activity's menu item
        when (activity) {
            is HomeActivity -> navView.selectedItemId = R.id.nav_home
            is SearchActivity -> navView.selectedItemId = R.id.nav_search
            is BoardActivity -> navView.selectedItemId = R.id.nav_board
            is ChatBot -> navView.selectedItemId = R.id.nav_user
            is CameraActivity -> navView.selectedItemId = R.id.nav_camera
        }
    }
}
