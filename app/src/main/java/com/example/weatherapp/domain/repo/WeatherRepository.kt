package com.example.weatherapp.domain.repo

import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {

    fun getCurrentWeather(city: String? = null, lat: Double?=null, lon: Double?=null): Single<Result<CurrentWeather>>
    fun getForecastWeather(city: String? = null, lat: Double?=null, lon: Double?=null): Single<Result<ForecastWeather>>
    fun getAirPollution(lat: Double, lon: Double): Single<Result<AirPollution>>
}