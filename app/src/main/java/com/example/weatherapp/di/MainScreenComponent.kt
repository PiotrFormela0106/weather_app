package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.ui.additionalinfo.AdditionalInfoScreenFragment
import com.example.weatherapp.ui.main.MainScreenFragment
import com.example.weatherapp.ui.main.MainScreenViewModel
import com.example.weatherapp.ui.settings.SettingsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[RepositoryModule::class])
interface MainScreenComponent {
    fun inject(fragment: MainScreenFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: AdditionalInfoScreenFragment)
}