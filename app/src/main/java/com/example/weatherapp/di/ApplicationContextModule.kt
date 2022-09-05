package com.example.weatherapp.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class])
class ApplicationContextModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}
