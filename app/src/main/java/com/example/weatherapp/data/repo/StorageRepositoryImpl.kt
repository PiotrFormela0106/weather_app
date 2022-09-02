package com.example.weatherapp.data.repo

import com.example.weatherapp.data.controller.PreferencesController
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toUnits
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import javax.inject.Inject

private const val EXTRA_CITY = "extra_city"
private const val EXTRA_LOCATION_METHOD = "location_method"
private const val EXTRA_UNITS = "units"
private const val EXTRA_LAT = "lat"
private const val EXTRA_LON = "lon"
private const val EXTRA_PLACE_ID = "place_id"

class StorageRepositoryImpl @Inject constructor(preferencesController: PreferencesController) :
    StorageRepository {
    private val prefs = preferencesController.prefs

    override fun saveCity(city: String?) {
        city?.takeIf { it.isNotEmpty() }
            ?.let {
                prefs.edit().putString(EXTRA_CITY, it).apply()
            }
    }

    override fun getCity(): String {
        return prefs.getString(EXTRA_CITY, "")?.takeIf { it.isNotEmpty() }.toString()
    }

    override fun saveLocationMethod(method: LocationMethod) {
        prefs.edit().putString(EXTRA_LOCATION_METHOD, method.toString()).apply()
    }

    override fun getLocationMethod(): LocationMethod {
        val method = prefs.getString(EXTRA_LOCATION_METHOD, LocationMethod.Location.toString()).orEmpty()
        return LocationMethod.valueOf(method)
    }

    override fun saveUnits(units: Units) {
        prefs.edit().putString(EXTRA_UNITS, units.toData()).apply()
    }

    override fun getUnits(): Units {
        return prefs.getString(EXTRA_UNITS, "").orEmpty().toUnits()
    }

    override fun saveCoordinates(lat: Double, lon: Double) {
        prefs.edit().putFloat(EXTRA_LAT, lat.toFloat()).apply()
        prefs.edit().putFloat(EXTRA_LON, lon.toFloat()).apply()
    }

    override fun getCoordinates(): Pair<Double, Double> {
        return Pair(prefs.getFloat(EXTRA_LAT, 0F).toDouble(),prefs.getFloat(EXTRA_LON, 0F).toDouble())
    }

    override fun savePlaceId(placeId: String) {
        prefs.edit().putString(EXTRA_PLACE_ID, placeId).apply()
    }

    override fun getPlaceId(): String {
        return prefs.getString(EXTRA_PLACE_ID, "").orEmpty()
    }

}
