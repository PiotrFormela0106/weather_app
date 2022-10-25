package com.example.city_data.repo

import android.content.Context
import com.example.base.Result
import com.example.city_data.mappers.toData
import com.example.city_data.mappers.toDomain
import com.example.city_domain.models.City
import com.example.city_domain.repo.CityRepository
import com.example.weather_domain.toError
import java.lang.Exception
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(context: Context) : CityRepository {
    private var cityDao: com.example.city_data.room.CityDao

    init {
        val database = com.example.city_data.room.CityDatabase
            .getInstance(context.applicationContext)
        cityDao = database.cityDao()
    }

    override suspend fun insertCity(cityName: String, photoId: String) {
        return cityDao.insert(com.example.city_data.room.City(city = cityName, photoId = photoId))
    }

    override suspend fun deleteCity(city: City) {
        return cityDao.delete(city.toData())
    }

    override suspend fun fetchCities(): Result<List<City>> {
        return try {
            val result = cityDao.getAllCities()
            Result.withValue(result.map { it.toDomain() })
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
