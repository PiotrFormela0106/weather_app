package com.example.weatherapp.domain.repo

import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.models.Units

interface StorageRepository {
    fun saveCity(city: String?)
    fun getCity(): String
    fun saveLocationMethod(method: LocationMethod)
    fun getLocationMethod(): LocationMethod
    fun saveUnits(units: Units)
    fun getUnits(): Units
    fun saveCoordinates(lat: Double, lon: Double)
    fun getLatitude(): Double
    fun getLongitude(): Double
}