//package com.example.weatherapp.core.main
//
//import androidx.test.core.app.ApplicationProvider
//import com.example.weatherapp.data.repo.StorageRepositoryImpl
//import io.mockk.MockKAnnotations
//import io.mockk.impl.annotations.RelaxedMockK
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.Before
//import org.junit.Test
//import com.google.common.truth.Truth.assertThat
//import android.content.res.Resources
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import org.junit.Rule
//
//class SettingsViewModelTest {
//
//    @ExperimentalCoroutinesApi
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var viewModel: com.example.settings_ui.SettingsViewModel
//    lateinit var storageRepository: StorageRepositoryImpl
//    @RelaxedMockK
//    lateinit var resources: Resources
//
//    @Before
//    fun setup() {
//        MockKAnnotations.init(this)
//        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
//        viewModel = com.example.settings_ui.SettingsViewModel(storageRepository, resources)
//    }
//
//    @Test
//    fun switchUnitsToNotMetric(){
//        val units = storageRepository.saveUnits(com.example.weather_domain.models.Units.Metric)
//        viewModel.switchUnitsClick()
//        viewModel.selectionUnits.observeForever { currentUnits ->
//            assertThat(currentUnits).isNotEqualTo(units)
//        }
//    }
//
//    @Test
//    fun switchUnitsToMetric(){
//        val units = storageRepository.saveUnits(com.example.weather_domain.models.Units.NotMetric)
//        viewModel.switchUnitsClick()
//        viewModel.selectionUnits.observeForever { currentUnits ->
//            assertThat(currentUnits).isNotEqualTo(units)
//        }
//    }
//}