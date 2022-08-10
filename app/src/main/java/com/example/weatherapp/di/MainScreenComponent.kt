package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.ui.main.MainScreenFragment
import com.example.weatherapp.ui.main.MainScreenViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[RepositoryModule::class])
interface MainScreenComponent {
    fun inject(fragment: MainScreenFragment)
}