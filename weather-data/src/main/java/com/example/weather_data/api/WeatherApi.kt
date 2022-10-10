package com.example.weather_data.api

import com.example.weather_data.models.AirPollution
import com.example.weather_data.models.CurrentWeather
import com.example.weather_data.models.ForecastWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeatherForCity(
        @Query("q") cityName: String?,
        @Query("appid") apikey: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): CurrentWeather

    @GET("weather")
    suspend fun getCurrentWeatherForLocation(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") apikey: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): CurrentWeather

    @GET("forecast")
    suspend fun getForecastForCity(
        @Query("q") cityName: String?,
        @Query("appid") apikey: String?,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): ForecastWeather

    @GET("forecast")
    suspend fun getForecastForLocation(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") apikey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): ForecastWeather

    @GET("air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apikey: String,
    ): AirPollution
}
