package com.example.weatherapp.data.controller

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesController @Inject constructor(context: Context) {
    val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
}
