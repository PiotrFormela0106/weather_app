package com.example.weatherapp.data.repo

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toLanguage
import com.example.weatherapp.data.mappers.toUnits
import com.example.weatherapp.domain.models.Language
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import java.util.*
import javax.inject.Inject

private const val EXTRA_CITY = "extra_city"
private const val EXTRA_LOCATION_METHOD = "location_method"
private const val EXTRA_UNITS = "units"
private const val EXTRA_LAT = "lat"
private const val EXTRA_LON = "lon"
private const val EXTRA_LANGUAGE = "language"
private const val EXTRA_PHOTO_ID = "photo_id"

class StorageRepositoryImpl @Inject constructor(context: Context) :
    StorageRepository {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

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
        val method =
            prefs.getString(EXTRA_LOCATION_METHOD, LocationMethod.Location.toString()).orEmpty()
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
        return Pair(
            prefs.getFloat(EXTRA_LAT, 0F).toDouble(),
            prefs.getFloat(EXTRA_LON, 0F).toDouble()
        )
    }

    override fun saveLanguage(lang: Language) {
        prefs.edit().putString(EXTRA_LANGUAGE, lang.toData()).apply()
    }

    override fun getLanguage(): Language {
        return prefs.getString(EXTRA_LANGUAGE, Locale.getDefault().language).orEmpty().toLanguage()
    }

    override fun savePhotoId(photoId: String) {
        prefs.edit().putString(EXTRA_PHOTO_ID, photoId).apply()
    }

    override fun getPhotoId(): String {
        return prefs.getString(EXTRA_PHOTO_ID, "").orEmpty()
    }
}
