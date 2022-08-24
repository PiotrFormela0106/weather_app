package com.example.weatherapp.data.repo

import androidx.core.content.edit
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.controller.PreferencesController.Companion.LAT
import com.example.weatherapp.controller.PreferencesController.Companion.LON
import com.example.weatherapp.controller.PreferencesController.Companion.UNITS
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toUnits
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod
import com.example.weatherapp.domain.models.Units
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

    override fun saveUnits(units: Units) {
        prefs.edit().putString(UNITS, units.toData()).apply()
    }

    override fun getUnits(): Units {
        return prefs.getString(UNITS, "").orEmpty().toUnits()
    }

    override fun saveCoordinates(lat: Double, lon: Double) {
        prefs.edit().putString(LAT, lat.toString()).apply()
        prefs.edit().putString(LON, lon.toString()).apply()
    }

    override fun getLat(): Double {
        return prefs.getString(LAT, "")!!.toDouble()
    }

    override fun getLon(): Double {
        return prefs.getString(LON, "")!!.toDouble()
    }
}


