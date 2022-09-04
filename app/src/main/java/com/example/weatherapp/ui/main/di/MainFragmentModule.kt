package com.example.weatherapp.ui.main.di

import androidx.lifecycle.ViewModel
import com.example.weatherapp.di.ViewModelKey
import com.example.weatherapp.ui.main.MainScreenFragment
import com.example.weatherapp.ui.main.MainScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [MainScreenViewModelModule::class])
abstract class MainFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainScreenFragment
}

@Module
abstract class MainScreenViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainScreenViewModel::class)
    abstract fun bindMainScreenViewModel(fragmentViewModel: MainScreenViewModel): ViewModel
}