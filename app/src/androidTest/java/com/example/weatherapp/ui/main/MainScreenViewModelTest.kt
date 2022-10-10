package com.example.weatherapp.ui.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_data.api.RetrofitClient
import com.example.weatherapp.data.repo.StorageRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weather_domain.models.LocationMethod
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainScreenViewModelTest{

    @ExperimentalCoroutinesApi
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: com.example.main_ui.MainScreenViewModel
    lateinit var storageRepository: StorageRepositoryImpl
    @RelaxedMockK
    lateinit var resources: Resources

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
        val retrofitClient = com.example.weather_data.api.RetrofitClient()
        val weatherRepository = WeatherRepositoryImpl(retrofitClient, storageRepository)
        viewModel =
            com.example.main_ui.MainScreenViewModel(weatherRepository, storageRepository, resources)
    }

    @Test
    fun checkWeatherDataResponseForCity() = runTest{
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.City)
        storageRepository.saveCity("Warsaw")
        viewModel.fetchData()
        viewModel.weatherData.observeForever { data ->
            assertThat(data.cityName).isEqualTo("Warsaw")
        }
    }
    @Test
    fun checkWeatherDataResponseForLocation() = runTest{
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.Location)
        storageRepository.saveCoordinates(54.27,18.19)
        viewModel.fetchData()
        viewModel.weatherData.observeForever { data ->
            assertThat(data.cityName).isNotNull()
        }
    }
    @Test
    fun checkForecastDataResponseForCity() = runTest{
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.City)
        storageRepository.saveCity("Warsaw")
        viewModel.fetchData()
        viewModel.forecastData.observeForever { data ->
            assertThat(data.city).isEqualTo("Warsaw")
        }
    }
    @Test
    fun checkForecastDataResponseForLocation() = runTest{
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.Location)
        storageRepository.saveCoordinates(54.27,18.19)
        viewModel.fetchData()
        viewModel.forecastData.observeForever { data ->
            assertThat(data.city).isNotNull()
        }
    }
    @Test
    fun checkAirPollutionDataResponse() = runTest{
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.City)
        storageRepository.saveCoordinates(54.27,18.19)
        viewModel.fetchData()
        viewModel.airPollutionData.observeForever { data ->
            assertThat(data.coordinates.lat).isEqualTo(54.27)
        }
    }

}