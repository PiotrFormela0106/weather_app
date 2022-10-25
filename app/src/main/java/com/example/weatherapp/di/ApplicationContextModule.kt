package com.example.weatherapp.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationContextModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
    @Provides
    fun provideResources(application: Application): Resources {
        return application.resources
    }
}
