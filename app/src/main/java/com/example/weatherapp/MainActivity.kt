package com.example.weatherapp

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(navController = findNavController(R.id.main_screen_fragment))

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_screen_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}
