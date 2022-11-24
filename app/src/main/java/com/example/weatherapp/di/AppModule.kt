package com.example.weatherapp.di

import android.app.Application
import android.content.res.Resources
import com.example.city_data.repo.CityRepositoryImpl
import com.example.city_domain.repo.CityRepository
import com.example.info_ui.AdditionalInfoScreenViewModel
import com.example.location_ui.CityScreenViewModel
import com.example.location_ui.MapsViewModel
import com.example.main_ui.MainScreenViewModel
import com.example.settings_ui.SettingsViewModel
import com.example.storage_data.repo.StorageRepositoryImpl
import com.example.storage_domain.repo.StorageRepository
import com.example.weather_data.api.RetrofitClient
import com.example.weather_data.repo.WeatherRepositoryImpl
import com.example.weather_domain.repo.WeatherRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    fun provideResources(application: Application): Resources {
        return application.resources
    }
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
    single<CityRepository> { CityRepositoryImpl(get()) }
    single { RetrofitClient() }
    single { provideResources(get()) }

}

val viewModelsModule = module {
    viewModel { MainScreenViewModel(get(), get(), get()) }
    viewModel { CityScreenViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { AdditionalInfoScreenViewModel(get()) }
    viewModel { MapsViewModel(get()) }
}
