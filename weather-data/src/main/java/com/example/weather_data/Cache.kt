package com.example.weather_data

import com.example.weather_domain.models.CurrentWeather
import com.example.weather_domain.models.ForecastWeather
import java.util.Calendar
import kotlin.collections.HashMap

private const val timeOut = 5 * 60 * 1000

class Cache {
    private val map = HashMap<CacheKey, ForecastWeather>()
    private val mapCurrentWeather = HashMap<CacheKey, CurrentWeather>()

    private val timeMap = HashMap<CacheKey, Long>()

    fun cache(key: CacheKey, forecast: ForecastWeather) {
        map[key] = forecast
        timeMap[key] = Calendar.getInstance().timeInMillis
    }
    fun cache(key: CacheKey, weather: CurrentWeather) {
        mapCurrentWeather[key] = weather
        timeMap[key] = Calendar.getInstance().timeInMillis
    }

    fun getForecast(key: CacheKey): ForecastWeather? {
        val value = map[key]
        if (value != null) {
            val timestamp = timeMap[key] ?: return null
            val currentTimestamp = Calendar.getInstance().timeInMillis
            if (currentTimestamp - timestamp > timeOut)
                return null
        }
        return value
    }

    fun getCurrentWeather(key: CacheKey): CurrentWeather? {
        val value = mapCurrentWeather[key]
        if (value != null) {
            val timestamp = timeMap[key] ?: return null
            val currentTimestamp = Calendar.getInstance().timeInMillis
            if (currentTimestamp - timestamp > timeOut)
                return null
        }
        return value
    }
}

internal fun getCityCacheKey(city: String, units: String, lang: String): CacheKey.CityCacheKey {
    return CacheKey.CityCacheKey(city, units, lang)
}

internal fun getLocationCacheKey(
    lat: Double,
    lon: Double,
    units: String,
    lang: String
): CacheKey.LocationCacheKey {
    return CacheKey.LocationCacheKey(lat, lon, units, lang)
}

sealed class CacheKey {
    data class CityCacheKey(val cityName: String, val units: String, val lang: String) : CacheKey()
    data class LocationCacheKey(
        val lat: Double,
        val lon: Double,
        val units: String,
        val lang: String
    ) : CacheKey()
}
