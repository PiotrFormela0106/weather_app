package com.example.weatherapp.domain.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.weatherapp.data.room.City
import com.example.weatherapp.data.room.CityDatabase
import io.reactivex.rxjava3.core.Single
import com.example.weatherapp.domain.Result
import io.reactivex.rxjava3.core.Completable

interface CityRepository {
    fun insertCity(cityName: String): Completable
    fun deleteCity(city: City): Completable
    fun updateCities(): Single<Result<List<City>>>
    fun deleteAllCities(): Completable
    fun getCurrentCity(): Single<Result<City>>
    fun deleteCityById(cityId: Int): Completable
}