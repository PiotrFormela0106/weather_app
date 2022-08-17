package com.example.weatherapp.di

import android.app.Application
import android.content.Context
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.repo.StorageRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule(val context: Context) {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        retrofitClient: RetrofitClient,
        storageRepository: StorageRepository
    ): WeatherRepository{
        return WeatherRepositoryImpl(
            retrofitClient = retrofitClient,
            storageRepository = storageRepository
        )
    }
    @Singleton
    @Provides
    fun provideStorageRepository(
        preferencesController: PreferencesController
    ): StorageRepository{
        return StorageRepositoryImpl(
            preferencesController = preferencesController
        )
    }

    @Provides
    fun providePreferences(): PreferencesController{
        return PreferencesController(context)
    }
}