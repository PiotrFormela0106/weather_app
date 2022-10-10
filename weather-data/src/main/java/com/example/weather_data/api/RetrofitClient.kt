package com.example.weather_data.api

import com.example.weather_data.BuildConfig.API_URL
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RetrofitClient @Inject constructor() {
    val api: WeatherApi = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build()
        .create(WeatherApi::class.java)
}
