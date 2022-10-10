package com.example.location_ui.di

import androidx.lifecycle.ViewModel
import com.example.location_ui.CityScreenFragment
import com.example.location_ui.CityScreenViewModel
import com.example.weatherapp.di.ViewModelKey
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
