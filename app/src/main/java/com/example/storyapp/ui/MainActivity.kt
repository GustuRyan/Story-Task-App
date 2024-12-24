package com.example.storyapp.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        if (isLoggedIn) {
            navController.navigate(R.id.homeFragment)
        } else {
            navController.navigate(R.id.startedFragment)
        }
    }
}