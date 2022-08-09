package com.example.weatherapp.di

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.domain.repo.WeatherRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        retrofitClient: RetrofitClient
    ): WeatherRepository{
        return WeatherRepositoryImpl(
            retrofitClient = retrofitClient
        )
    }
}