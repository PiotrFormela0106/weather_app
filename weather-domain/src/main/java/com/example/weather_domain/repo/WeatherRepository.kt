package com.example.weather_domain.repo

import com.example.weather_domain.models.AirPollution
import com.example.weather_domain.models.CurrentWeather
import com.example.weather_domain.models.ForecastWeather
import  com.example.base.Result

interface WeatherRepository {

    suspend fun getCurrentWeather(): Result<CurrentWeather>
    suspend fun getForecastWeather(): Result<ForecastWeather>
    suspend fun getAirPollution(): Result<AirPollution>
}
