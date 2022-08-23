package com.example.weatherapp.controller

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toUnits
import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod
import com.example.weatherapp.domain.models.Units
import javax.inject.Inject

class PreferencesController @Inject constructor(context: Context) {
    val prefs: SharedPreferences =
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

    fun saveLocationMethod(method: LocationMethod) {
        prefs.edit().putString("location_method", method.toString()).apply()
    }

    fun getLocationMethod(): LocationMethod {
        val method = prefs.getString("location_method", "").orEmpty()
        return LocationMethod.toLocationMethod(method)
    }

    fun saveUnits(units: Units) {
        prefs.edit().putString(UNITS, units.toData()).apply()
    }

    fun getUnits(): Units {
        return prefs.getString(UNITS, "").orEmpty().toUnits()
    }



    companion object {
        const val EXTRA_CITY = "extra_city"
        const val LOCATION_METHOD = "location_method"
        const val UNITS = "units"
    }
}