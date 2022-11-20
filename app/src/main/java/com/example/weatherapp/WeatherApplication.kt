package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.appModule
import com.example.weatherapp.di.viewModelsModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApplication)
            modules(
                listOf(
                    appModule,
                    viewModelsModule
                )
            )
        }
    }
}
