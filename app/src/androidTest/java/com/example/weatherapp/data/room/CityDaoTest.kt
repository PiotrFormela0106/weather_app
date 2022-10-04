package com.example.weatherapp.data.room

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CityDaoTest {
    private lateinit var database: CityDatabase
    private lateinit var dao: CityDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CityDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.cityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCityItem() = runTest {
        //adding one element to db
        val cityItem1 = City(cityId = 1, city = "Gdansk", photoId = "http://....")
        dao.insert(cityItem1)
        val allCities = dao.getAllCities()
        assertThat(allCities).contains(cityItem1)
    }

    @Test
    fun removeCityItem() = runTest {
        //removing one element from db
        val cityItem1 = City(cityId = 1, city = "Gdansk", photoId = "http://....")
        dao.insert(cityItem1)
        dao.delete(cityItem1)
        val allCities = dao.getAllCities()
        assertThat(allCities).isEmpty()
    }

    @Test
    fun deleteCities() = runTest {
        //Removing all elements from db
        val cityItem1 = City(city = "Gdansk", photoId = "http://....")
        val cityItem2 = City(city = "Gdynia", photoId = "http://....")
        val cityItem3 = City(city = "Sopot", photoId = "http://....")
        dao.insert(cityItem1)
        dao.insert(cityItem2)
        dao.insert(cityItem3)
        dao.deleteAllCities()
        val allCities = dao.getAllCities()
        assertThat(allCities).isEmpty()
    }

    @Test
    fun getPhotoId() = runTest {
        //getting photo id
        val cityItem1 = City(cityId = 1, city = "Gdansk", photoId = "http://...")
        dao.insert(cityItem1)
        val photo = dao.getPhotoId(cityItem1.city)
        assertThat(photo).isEqualTo("http://...")
    }


}