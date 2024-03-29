package com.example.weatherapp.domain.repo

import com.example.weatherapp.domain.models.Language
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
    fun getCoordinates(): Pair<Double, Double>
    fun saveLanguage(lang: Language)
    fun getLanguage(): Language
    fun savePhotoId(photoId: String)
    fun getPhotoId(): String
}
