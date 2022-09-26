package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.room.City
import com.example.weatherapp.domain.Result

interface CityRepository {
    suspend fun insertCity(cityName: String, photoId: String)
    suspend fun deleteCity(city: City)
    suspend fun fetchCities(): Result<List<City>>
    suspend fun deleteAllCities()
    suspend fun getPhotoId(city: String): Result<String>
}
