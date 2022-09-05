package com.example.weatherapp.di

import com.example.weatherapp.MainActivity
import com.example.weatherapp.ui.di.FragmentsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [FragmentsModule::class])
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}
