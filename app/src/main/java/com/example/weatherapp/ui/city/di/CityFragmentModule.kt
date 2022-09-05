package com.example.weatherapp.ui.city.di

import androidx.lifecycle.ViewModel
import com.example.weatherapp.di.ViewModelKey
import com.example.weatherapp.ui.city.CityScreenFragment
import com.example.weatherapp.ui.city.CityScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [CityScreenViewModelModule::class])
abstract class CityFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeCityScreenFragment(): CityScreenFragment
}

@Module
abstract class CityScreenViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CityScreenViewModel::class)
    abstract fun bindCityScreenViewModel(fragmentViewModel: CityScreenViewModel): ViewModel
}
