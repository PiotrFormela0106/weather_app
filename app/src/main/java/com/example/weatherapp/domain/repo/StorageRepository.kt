package com.example.weatherapp.domain.repo

import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.models.Units

interface StorageRepository {
    fun saveCity(city: String?)//we should remove this fun in current version because we can get city from database
    fun getCity(): String//we should remove this fun in current version because we can get city from database
    fun saveLocationMethod(method: LocationMethod)
    fun getLocationMethod(): LocationMethod
    fun saveUnits(units: Units)
    fun getUnits(): Units
    fun saveCoordinates(lat: Double, lon: Double)
    fun getLatitude(): Double//change this ti fun getCoordinates(): Pair<Double, Double>
    fun getLongitude(): Double//the same
}