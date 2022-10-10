package com.example.weatherapp.di

import android.content.Context
import com.example.weather_data.api.RetrofitClient
import com.example.city_data.repo.CityRepositoryImpl
import com.example.city_domain.repo.CityRepository
import com.example.storage_data.repo.StorageRepositoryImpl
import com.example.storage_domain.repo.StorageRepository
import com.example.weather_data.repo.WeatherRepositoryImpl
import com.example.weather_domain.repo.WeatherRepository
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
