package com.example.weatherapp.data.repo

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.domain.models.LocationMethod
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.domain.models.Language
import com.example.weatherapp.domain.models.Units

@RunWith(AndroidJUnit4::class)
class StorageRepositoryImplTest {

    lateinit var storageRepository: StorageRepositoryImpl

    @Before
    fun setup() {
        storageRepository = StorageRepositoryImpl(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun savingCity(){
        storageRepository.saveCity("Krakow")
        val city = storageRepository.getCity()
        assertThat(city).isEqualTo("Krakow")
    }

    @Test
    fun savingLocationMethod(){
        storageRepository.saveLocationMethod(LocationMethod.City)
        val locationMethod = storageRepository.getLocationMethod().toString()
        assertThat(locationMethod).isEqualTo("City")
    }

    @Test
    fun savingUnits(){
        storageRepository.saveUnits(Units.Metric)
        val units = storageRepository.getUnits().toData()
        assertThat(units).isEqualTo("Metric")
    }

    @Test
    fun savingLanguage(){
        storageRepository.saveLanguage(Language.PL)
        val lang = storageRepository.getLanguage().toData().uppercase()
        assertThat(lang).isEqualTo("PL")
    }

    @Test
    fun savingCoordinates(){
        storageRepository.saveCoordinates(54.27,18.19)
        val coordinates = storageRepository.getCoordinates()
        assertThat(coordinates.first).isAtLeast(54.27)
    }

    @Test
    fun savingPhotoId(){
        storageRepository.savePhotoId("http:/...")
        val photoId = storageRepository.getPhotoId()
        assertThat(photoId).isEqualTo("http:/...")
    }
}