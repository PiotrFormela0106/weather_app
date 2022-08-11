package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.mappers.CurrentWeatherDomain
import com.example.weatherapp.data.mappers.ForecastWeatherDomain
import com.example.weatherapp.domain.Result
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
    fun getCurrentWeatherForCity(city: String): Single<Result<CurrentWeatherDomain>>
    fun getCurrentWeatherForLocation(lat: Double, lon: Double): Single<Result<CurrentWeatherDomain>>
    fun getForecastForCity(cityName: String): Single<Result<ForecastWeatherDomain>>
    fun getForecastForLocation(lat: Double, lon: Double): Single<Result<ForecastWeatherDomain>>
}