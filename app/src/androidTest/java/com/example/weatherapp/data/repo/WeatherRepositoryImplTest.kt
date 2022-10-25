package com.example.weatherapp.data.repo

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherRepositoryImplTest {

    lateinit var weatherRepository: WeatherRepositoryImpl

    lateinit var storageRepository: StorageRepositoryImpl

    @Before
    fun setup() {
        val retrofitClient = com.example.weather_data.api.RetrofitClient()
        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
        weatherRepository = WeatherRepositoryImpl(retrofitClient, storageRepository)
    }

    @Test
    fun getCurrentWeather() = runTest {
        when (val result = weatherRepository.getCurrentWeather()) {
            is com.example.weather_domain.Result.OnSuccess -> {
                assertThat(result.data).isNotNull()
            }
            is com.example.weather_domain.Result.OnError -> {}
        }
    }

    @Test
    fun checkResponse() = runTest {
        lateinit var data: com.example.weather_domain.models.CurrentWeather
        storageRepository.saveLanguage(com.example.weather_domain.models.Language.ENG)
        storageRepository.saveLocationMethod(com.example.weather_domain.models.LocationMethod.City)
        storageRepository.saveCity("Warsaw")
        when (val result = weatherRepository.getCurrentWeather()) {
            is com.example.weather_domain.Result.OnSuccess -> {
                data = result.data
            }
            is com.example.weather_domain.Result.OnError -> {}
        }
        assertThat(data.cityName).isEqualTo("Warsaw")
    }

    @Test
    fun getForecastWeather() = runTest {
        lateinit var data: com.example.weather_domain.models.ForecastWeather
        when (val result = weatherRepository.getForecastWeather()) {
            is com.example.weather_domain.Result.OnSuccess -> {
                data = result.data
            }
            is com.example.weather_domain.Result.OnError -> {}
        }
        assertThat(data).isNotNull()
    }

    @Test
    fun getAirPollution() = runTest {
        lateinit var data: com.example.weather_domain.models.AirPollution
        storageRepository.saveCoordinates(54.27, 18.19)
        when (val result = weatherRepository.getAirPollution()) {
            is com.example.weather_domain.Result.OnSuccess -> {
                data = result.data
            }
            is com.example.weather_domain.Result.OnError -> {}
        }
        assertThat(data.coordinates.lat).isEqualTo(54.27)
    }
}
