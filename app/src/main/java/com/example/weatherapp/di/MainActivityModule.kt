package com.example.weatherapp.di

import com.example.weatherapp.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.example.weatherapp.ui.di.FragmentsModule

@Module(includes = [FragmentsModule::class])
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}
