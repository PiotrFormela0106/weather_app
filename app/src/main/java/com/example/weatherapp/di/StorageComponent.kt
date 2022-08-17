package com.example.weatherapp.di

import com.example.weatherapp.ui.settings.SettingsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[RepositoryModule::class])
interface StorageComponent {
    fun inject(fragment: SettingsFragment)
}