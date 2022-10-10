package com.example.city_domain.repo

import com.example.city_domain.models.City
import com.example.weather_domain.Result

interface CityRepository {
    suspend fun insertCity(cityName: String, photoId: String)
    suspend fun deleteCity(city: City)
    suspend fun fetchCities(): Result<List<City>>
    suspend fun deleteAllCities()
    suspend fun getPhotoId(city: String): Result<String>
}
