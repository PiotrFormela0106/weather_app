package com.example.weatherapp.data.api

import com.example.weatherapp.data.models.AirPollution
import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.models.ForecastWeather
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getCurrentWeatherForCity(
        @Query("q") cityName: String?,
        @Query("appid") apikey: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    )
            : Single<CurrentWeather>

    @GET("weather")
    fun getCurrentWeatherForLocation(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") apikey: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    )       : Single<CurrentWeather>


    @GET("forecast")
    fun getForecastForCity(
        @Query("q") cityName: String?,
        @Query("appid") apikey: String?,
        @Query("lang") lang: String,
        @Query("units") units: String
    )
            : Single<ForecastWeather>

    @GET("forecast")
    fun getForecastForLocation(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") apikey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    )
            : Single<ForecastWeather>

    @GET("air_pollution")
    fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apikey: String,
    ):Single<AirPollution>
}