package com.example.weatherapp.data

import com.example.weatherapp.domain.models.ForecastWeather

class Cache {
    //modify class Cache to add here expiration by time
    //use timestamps

    //create other Map<CacheKey, String(timestamp)>
    //put timestamp in map when you cache
    //check timestamp and timeout when you get from cache
    //timeout = 5 minutes
    private val map = HashMap<CacheKey, ForecastWeather>()
    fun cache(key: CacheKey, forecast: ForecastWeather) {
        map[key] = forecast
    }

    fun getForecast(key: CacheKey): ForecastWeather? {
        return map[key]
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
