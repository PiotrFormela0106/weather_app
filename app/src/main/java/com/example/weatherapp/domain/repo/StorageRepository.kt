package com.example.weatherapp.domain.repo

interface StorageRepository {
    fun saveCity(cityName: String)
}