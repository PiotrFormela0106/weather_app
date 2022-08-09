package com.example.weatherapp.di

import com.example.weatherapp.ui.main.MainScreenFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[RepositoryModule::class])
interface MainScreenComponent {
    fun inject(fragment: MainScreenFragment)
}