package com.example.settings_ui.di

import androidx.lifecycle.ViewModel
import com.example.di.ViewModelKey
import com.example.settings_ui.SettingsFragment
import com.example.settings_ui.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [SettingsViewModelModule::class])
abstract class SettingsFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment
}

@Module
abstract class SettingsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(fragmentViewModel: SettingsViewModel): ViewModel
}
