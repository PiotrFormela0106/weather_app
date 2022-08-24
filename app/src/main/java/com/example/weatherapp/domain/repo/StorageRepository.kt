package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod
import com.example.weatherapp.domain.models.Units

interface StorageRepository {
    fun saveCity(cityName: String?)//remove, use function of CityRepository to save city to database
    fun getCity(): String?//remove, use function of CityRepository to get city from database
    fun saveLocationMethod(method: LocationMethod)
    fun getLocationMethod(): LocationMethod
    fun saveUnits(units: Units)
    fun getUnits(): Units
    fun saveCoordinates(lat: Double, lon: Double)
    fun getLat(): Double
    fun getLon(): Double
}