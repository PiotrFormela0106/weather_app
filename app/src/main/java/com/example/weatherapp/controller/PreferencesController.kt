package com.example.weatherapp.controller

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesController @Inject constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    fun saveCity(city: String?) {
        if (city == null) {
            prefs.edit().remove(EXTRA_CITY).apply()
        } else {
            prefs.edit().putString(EXTRA_CITY, city).apply()
        }

    }

    fun getCity(): String {
        return prefs.getString(EXTRA_CITY, "").orEmpty()
    }

    companion object {
        private const val EXTRA_CITY = "extra_city"
    }
}