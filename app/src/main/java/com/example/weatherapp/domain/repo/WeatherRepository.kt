package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.mappers.CurrentWeatherDomain
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.Result
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
    fun getCurrentWeatherForCity(city: String): Single<Result<CurrentWeatherDomain>>
    //fun getCurrentWeatherForLocation(lat: Long, lon: Long): Single<Result<CurrentWeather>>
    //fun getForecastForCity(city: String): Single<Result<ForecastWeather>>
    //fun getForecastForLocation(lat: Long, lon: Long): Single<Result<ForecastWeather>>
}