package com.example.weatherapp.controller

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toUnits
import com.example.weatherapp.domain.models.Units
import javax.inject.Inject
//move the whole controller package inside data package
class PreferencesController @Inject constructor(context: Context) {
    val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    companion object {//move these constants in the file where they are used as private
        const val EXTRA_CITY = "extra_city"
        const val LOCATION_METHOD = "location_method"//rename EXTRA_LOCATION_METHOD
        const val UNITS = "units"//rename EXTRA_UNITS
        const val LAT = "lat"//rename EXTRA_LAT
        const val LON = "lon"//rename EXTRA_LON
    }
}