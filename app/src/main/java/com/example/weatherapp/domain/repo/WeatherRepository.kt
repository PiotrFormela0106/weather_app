package com.example.weatherapp.domain.repo

import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather

interface WeatherRepository {

    suspend fun getCurrentWeather(): Result<CurrentWeather>
    suspend fun getForecastWeather(): Result<ForecastWeather>
    suspend fun getAirPollution(): Result<AirPollution>
}
