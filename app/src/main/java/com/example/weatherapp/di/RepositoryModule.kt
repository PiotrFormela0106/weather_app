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
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
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
        @ApplicationContext context: Context
    ): StorageRepository {
        return StorageRepositoryImpl(context = context)
    }

    @Singleton
    @Provides
    fun provideCityRepository(@ApplicationContext context: Context): CityRepository {
        return CityRepositoryImpl(context = context)
    }
}
