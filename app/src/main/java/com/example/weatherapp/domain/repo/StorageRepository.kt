package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod

interface StorageRepository {
    fun saveCity(city: String?)
    fun getCity(): String
    fun saveLocationMethod(method: LocationMethod)
    fun getLocationMethod(): LocationMethod
    fun saveUnits(units: String)
    fun getUnits(): String
}