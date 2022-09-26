package com.example.weatherapp.data.repo

import android.content.Context
import com.example.weatherapp.data.room.City
import com.example.weatherapp.data.room.CityDao
import com.example.weatherapp.data.room.CityDatabase
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.repo.CityRepository
import com.example.weatherapp.domain.toError
import java.lang.Exception
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(context: Context) : CityRepository {
    private var cityDao: CityDao

    init {
        val database = CityDatabase
            .getInstance(context.applicationContext)
        cityDao = database.cityDao()
    }

    override suspend fun insertCity(cityName: String, photoId: String) {
        return cityDao.insert(City(city = cityName, photoId = photoId))
    }

    override suspend fun deleteCity(city: City) {
        return cityDao.delete(city)
    }

    override suspend fun fetchCities(): Result<List<City>> {
        return try {
            val result = cityDao.getAllCities()
            Result.withValue(result)
        } catch (ex: Exception) {
            ex.toResultError()
        }
    }

    override suspend fun deleteAllCities() {
        return cityDao.deleteAllCities()
    }

    override suspend fun getPhotoId(city: String): Result<String> {
        return try {
            val result = cityDao.getPhotoId(city)
            Result.withValue(result)
        } catch (ex: Exception) {
            ex.toResultError()
        }
    }

    private fun <T> Throwable.toResultError(): Result<T> {
        val error = this.toError()
        return Result.withError(error)
    }
}
