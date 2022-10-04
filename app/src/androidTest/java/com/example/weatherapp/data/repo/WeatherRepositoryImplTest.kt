package com.example.weatherapp.data.repo

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.domain.models.LocationMethod
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.Language
import com.google.common.truth.Truth.assertThat

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherRepositoryImplTest {

    lateinit var weatherRepository: WeatherRepositoryImpl

    lateinit var storageRepository: StorageRepositoryImpl

    @Before
    fun setup() {
        val retrofitClient = RetrofitClient()
        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
        weatherRepository = WeatherRepositoryImpl(retrofitClient, storageRepository)
    }

    @Test
    fun getCurrentWeather() = runTest {
        when (val result = weatherRepository.getCurrentWeather()) {
            is Result.OnSuccess -> {
                assertThat(result.data).isNotNull()
            }
            is Result.OnError -> {}
        }


    }

    @Test
    fun checkResponse() = runTest {
        lateinit var data: CurrentWeather
        storageRepository.saveLanguage(Language.ENG)
        storageRepository.saveLocationMethod(LocationMethod.City)
        storageRepository.saveCity("Warsaw")
        when (val result = weatherRepository.getCurrentWeather()) {
            is Result.OnSuccess -> {
                data = result.data
            }
            is Result.OnError -> {}
        }
        assertThat(data.cityName).isEqualTo("Warsaw")

    }

    @Test
    fun getForecastWeather() = runTest {
        lateinit var data: ForecastWeather
        when (val result = weatherRepository.getForecastWeather()) {
            is Result.OnSuccess -> {
                data = result.data
            }
            is Result.OnError -> {}
        }
        assertThat(data).isNotNull()

    }

    @Test
    fun getAirPollution() = runTest {
        lateinit var data: AirPollution
        storageRepository.saveCoordinates(54.27,18.19)
        when (val result = weatherRepository.getAirPollution()) {
            is Result.OnSuccess -> {
                data = result.data
            }
            is Result.OnError -> {}
        }
        assertThat(data.coordinates.lat).isEqualTo(54.27)

    }


}