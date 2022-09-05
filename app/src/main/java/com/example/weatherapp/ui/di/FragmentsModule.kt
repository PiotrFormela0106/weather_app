package com.example.weatherapp.ui.di

import com.example.weatherapp.ui.additionalinfo.di.AdditionalInfoFragmentsModule
import com.example.weatherapp.ui.city.di.CityFragmentModule
import com.example.weatherapp.ui.main.di.MainFragmentModule
import com.example.weatherapp.ui.settings.di.SettingsFragmentModule
import dagger.Module

@Module(
    includes = [
        MainFragmentModule::class,
        CityFragmentModule::class,
        AdditionalInfoFragmentsModule::class,
        SettingsFragmentModule::class
    ]
)
abstract class FragmentsModule
