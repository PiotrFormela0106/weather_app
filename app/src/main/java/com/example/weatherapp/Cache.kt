package com.example.weatherapp

import com.example.weatherapp.domain.models.ForecastWeather

class Cache {
    private val map = HashMap<String, ForecastWeather>()
    fun cache(key: String, forecast: ForecastWeather){
        map[key] = forecast
    }
    fun getForecast(key: String): ForecastWeather? {
        return map[key]
    }
    sealed class CacheKey{
        data class CityCacheKey(val cityName: String): CacheKey()
        //data class LocationCacheKey(val lat: Float, val lon: Float): CacheKey()
    }
}