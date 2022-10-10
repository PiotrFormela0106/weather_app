package com.example.weatherapp.ui.di

import com.example.info_ui.di.AdditionalInfoFragmentsModule
import com.example.location_ui.di.CityFragmentModule
import com.example.location_ui.di.MapsFragmentModule
import com.example.main_ui.di.MainFragmentModule
import com.example.settings_ui.di.SettingsFragmentModule
import dagger.Module

@Module(
    includes = [
        MainFragmentModule::class,
        CityFragmentModule::class,
        AdditionalInfoFragmentsModule::class,
        SettingsFragmentModule::class,
        MapsFragmentModule::class
    ]
)
abstract class FragmentsModule
