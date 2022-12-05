package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.firebase.FirebaseOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_WeatherApp)
        setContentView(binding.root)

        val trace = FirebasePerformance.getInstance().newTrace("OnCreateMainActivity")
        trace.start()
        val options = FirebaseOptions.Builder()
            .setProjectId("weatherapp-56c1f")
            .setApplicationId("1:329381951953:android:2f88bcea078d01483548f0")
            .setApiKey("AIzaSyCIEO79o1tCMFOQJb_KG8UPcjGiHh-VQsM")
            .build()

        Firebase.initialize(this, options, "WeatherApp")
        analytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("startApp", "onCreate")
        analytics.logEvent("startApp", bundle)

        trace.stop()

    }
}
