package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.mappers.AirPollutionDomain
import com.example.weatherapp.data.mappers.CurrentWeatherDomain
import com.example.weatherapp.data.mappers.ForecastWeatherDomain
import com.example.weatherapp.domain.Result
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {

    fun getCurrentWeather(city: String? = null, lat: Double?=null, lon: Double?=null): Single<Result<CurrentWeatherDomain>>
    fun getForecastWeather(city: String? = null, lat: Double?=null, lon: Double?=null): Single<Result<ForecastWeatherDomain>>
    fun getAirPollution(lat: Double, lon: Double): Single<Result<AirPollutionDomain>>
}