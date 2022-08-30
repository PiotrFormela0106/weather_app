package com.example.weatherapp.data.repo

import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.controller.PreferencesController.Companion.LAT
import com.example.weatherapp.controller.PreferencesController.Companion.LOCATION_METHOD
import com.example.weatherapp.controller.PreferencesController.Companion.LON
import com.example.weatherapp.controller.PreferencesController.Companion.UNITS
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toUnits
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.models.Units

import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(preferencesController: PreferencesController) :
    StorageRepository {
    private val prefs = preferencesController.prefs
    override fun saveCity(city: String?) {
        city?.takeIf { it.isNotEmpty() }
            ?.let {
                prefs.edit().putString(PreferencesController.EXTRA_CITY, it).apply()
            }
    }

    override fun getCity(): String {
        return prefs.getString(PreferencesController.EXTRA_CITY, "")?.takeIf { it.isNotEmpty() }.toString()
    }

    override fun saveLocationMethod(method: LocationMethod) {
        prefs.edit().putString(LOCATION_METHOD, method.toString()).apply()
    }

    override fun getLocationMethod(): LocationMethod {
        val method = prefs.getString(LOCATION_METHOD, LocationMethod.Location.toString()).orEmpty()
        return LocationMethod.valueOf(method)
    }

    override fun saveUnits(units: Units) {
        prefs.edit().putString(UNITS, units.toData()).apply()
    }

    override fun getUnits(): Units {
        return prefs.getString(UNITS, "").orEmpty().toUnits()
    }

    override fun saveCoordinates(lat: Double, lon: Double){
        prefs.edit().putFloat(LAT, lat.toFloat()).apply()
        prefs.edit().putFloat(LON, lon.toFloat()).apply()
    }
    override fun getLatitude(): Double{
        return prefs.getFloat(LAT, 0F).toDouble()
    }
    override fun getLongitude(): Double{
        return prefs.getFloat(LON, 0F).toDouble()
    }
}


