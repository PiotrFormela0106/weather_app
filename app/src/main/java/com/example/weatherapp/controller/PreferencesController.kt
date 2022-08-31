package com.example.weatherapp.controller

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesController @Inject constructor(context: Context) {
    val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    companion object {
        const val EXTRA_CITY = "extra_city"
        const val LOCATION_METHOD = "location_method"
        const val UNITS = "units"
        const val LAT = "lat"
        const val LON = "lon"
    }
}
