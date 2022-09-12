package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.repo.CityRepositoryImpl
import com.example.weatherapp.data.repo.StorageRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.domain.repo.CityRepository
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        retrofitClient: RetrofitClient,
        storageRepository: StorageRepository
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            retrofitClient = retrofitClient,
            storageRepository = storageRepository
        )
    }

    @Singleton
    @Provides
    fun provideStorageRepository(
        context: Context
    ): StorageRepository {
        return StorageRepositoryImpl(context = context)
    }

    @Singleton
    @Provides
    fun provideCityRepository(context: Context): CityRepository {
        return CityRepositoryImpl(context = context)
    }
}
