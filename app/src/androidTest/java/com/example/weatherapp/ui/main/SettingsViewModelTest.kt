package com.example.weatherapp.ui.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.repo.StorageRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.domain.models.LocationMethod
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
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.ui.settings.SettingsViewModel
import org.junit.Rule

class SettingsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SettingsViewModel
    lateinit var storageRepository: StorageRepositoryImpl
    @RelaxedMockK
    lateinit var resources: Resources

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
        viewModel = SettingsViewModel(storageRepository, resources)
    }

    @Test
    fun switchUnitsToNotMetric(){
        val units = storageRepository.saveUnits(Units.Metric)
        viewModel.switchUnitsClick()
        viewModel.selectionUnits.observeForever { currentUnits ->
            assertThat(currentUnits).isNotEqualTo(units)
        }
    }

    @Test
    fun switchUnitsToMetric(){
        val units = storageRepository.saveUnits(Units.NotMetric)
        viewModel.switchUnitsClick()
        viewModel.selectionUnits.observeForever { currentUnits ->
            assertThat(currentUnits).isNotEqualTo(units)
        }
    }
}