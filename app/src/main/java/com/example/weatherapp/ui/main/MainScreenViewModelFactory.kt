package com.example.weatherapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.domain.repo.WeatherRepository
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton

/*@Singleton
class MainScreenViewModelFactory @Inject constructor(val weatherRepositoryImpl: WeatherRepositoryImpl): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        /*if(modelClass == MainScreenViewModel::class.java){
            viewModel as T
        }else{
            throw IllegalStateException("Unknown entity")
        }*/

        if(modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel(weatherRepositoryImpl) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}*/