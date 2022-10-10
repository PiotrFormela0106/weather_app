package com.example.main_ui.di

import androidx.lifecycle.ViewModel
import com.example.main_ui.MainScreenFragment
import com.example.main_ui.MainScreenViewModel
import com.example.di.ViewModelKey
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
