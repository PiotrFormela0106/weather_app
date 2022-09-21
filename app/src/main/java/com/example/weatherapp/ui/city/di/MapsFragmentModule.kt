package com.example.weatherapp.ui.city.di

import androidx.lifecycle.ViewModel
import com.example.weatherapp.di.ViewModelKey
import com.example.weatherapp.ui.city.MapsFragment
import com.example.weatherapp.ui.city.MapsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [MapsViewModelModule::class])
abstract class MapsFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMapsFragment(): MapsFragment
}

@Module
abstract class MapsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    abstract fun bindMapsViewModel(fragmentViewModel: MapsViewModel): ViewModel
}
