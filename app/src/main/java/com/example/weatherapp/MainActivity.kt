package com.example.weatherapp


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.main.MainScreenFragmentDirections


class MainActivity : AppCompatActivity() {

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