package com.example.weatherapp.data.repo

import androidx.core.content.edit
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.controller.PreferencesController.Companion.UNITS
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(preferencesController: PreferencesController) :
    StorageRepository {
    private val prefs = preferencesController.prefs
    override fun saveCity(city: String?) {
        if (city == null) {
            prefs.edit().remove(PreferencesController.EXTRA_CITY).apply()
        } else {
            prefs.edit().putString(PreferencesController.EXTRA_CITY, city).apply()
        }
    }

    override fun getCity(): String {
        return prefs.getString(PreferencesController.EXTRA_CITY, "").orEmpty()
    }

    override fun saveLocationMethod(method: LocationMethod) {
        prefs.edit().putString("location_method", method.toString()).apply()
    }

    override fun getLocationMethod(): LocationMethod {
        val method = prefs.getString("location_method", "").orEmpty()
        return LocationMethod.toLocationMethod(method)
    }

    override fun saveUnits(units: String) {
        prefs.edit().putString(UNITS, units).apply()
    }

    override fun getUnits(): String {
        return prefs.getString(UNITS, "").orEmpty()
    }
}