package com.example.info_ui.di

import androidx.lifecycle.ViewModel
import com.example.weatherapp.di.ViewModelKey
import com.example.info_ui.AdditionalInfoScreenFragment
import com.example.info_ui.AdditionalInfoScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [AdditionalInfoScreenViewModelModule::class])
abstract class AdditionalInfoFragmentsModule {

    @ContributesAndroidInjector
    abstract fun contributeAdditionalInfoScreenFragment(): AdditionalInfoScreenFragment
}

@Module
abstract class AdditionalInfoScreenViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AdditionalInfoScreenViewModel::class)
    abstract fun bindAdditionalInfoScreenViewModel(fragmentViewModel: AdditionalInfoScreenViewModel): ViewModel
}
