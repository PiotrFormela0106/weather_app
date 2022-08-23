package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod
import com.example.weatherapp.domain.models.Units

interface StorageRepository {
    fun saveCity(city: String?)
    fun getCity(): String
    fun saveLocationMethod(method: LocationMethod)
    fun getLocationMethod(): LocationMethod
    fun saveUnits(units: Units)
    fun getUnits(): Units
}