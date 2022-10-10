package com.example.weatherapp.ui.main

import androidx.test.core.app.ApplicationProvider
import com.example.weather_data.api.RetrofitClient
import com.example.weatherapp.data.repo.StorageRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weather_domain.models.LocationMethod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.info_ui.AdditionalInfoScreenViewModel
import org.junit.Rule

@ExperimentalCoroutinesApi
class AdditionalInfoScreenViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: com.example.info_ui.AdditionalInfoScreenViewModel
    lateinit var storageRepository: StorageRepositoryImpl

    @Before
    fun setup(){
        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
        val retrofitClient = com.example.weather_data.api.RetrofitClient()
        val weatherRepository = WeatherRepositoryImpl(retrofitClient, storageRepository)
        viewModel = com.example.info_ui.AdditionalInfoScreenViewModel(weatherRepository)
    }

    @Test
    fun fetchData() = runTest{
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.City)
        storageRepository.saveCity("Warsaw")
        viewModel.fetchData()
        viewModel.forecast.observeForever { data ->
            assertThat(data?.city).isEqualTo("Warsaw")
        }
    }
}