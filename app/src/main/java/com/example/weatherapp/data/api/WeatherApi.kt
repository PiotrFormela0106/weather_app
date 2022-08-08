package com.example.weatherapp.data.api

import com.example.weatherapp.data.models.WeatherClass
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getData(
        @Query("q") cityName: String,
        @Query("appid") apikey: String
    )
            : Call<WeatherClass>

}