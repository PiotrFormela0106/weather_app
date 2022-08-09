package com.example.weatherapp.di

import com.example.weatherapp.data.api.WeatherApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideApiService(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}