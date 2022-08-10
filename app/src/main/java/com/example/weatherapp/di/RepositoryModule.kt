package com.example.weatherapp.di

import android.app.Application
import android.content.Context
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.domain.repo.WeatherRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule(val context: Context) {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        retrofitClient: RetrofitClient
    ): WeatherRepository{
        return WeatherRepositoryImpl(
            retrofitClient = retrofitClient
        )
    }

    @Provides
    fun providePreferences(): PreferencesController{
        return PreferencesController(context)
    }
}