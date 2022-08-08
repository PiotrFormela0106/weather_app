package com.example.weatherapp.data.api

import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.models.ForecastWeather
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getData(
        @Query("q") cityName: String,
        @Query("appid") apikey: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    )
            : Single<CurrentWeather>

    @GET("forecast")
    fun getForecast(
        @Query("q") cityName: String,
        @Query("appid") apikey: String
    )
            : Single<ForecastWeather>
}