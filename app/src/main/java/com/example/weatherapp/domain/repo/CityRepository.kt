package com.example.weatherapp.domain.repo

import com.example.weatherapp.data.room.City
import com.example.weatherapp.domain.Result
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface CityRepository {
    fun insertCity(cityName: String, placeId: String): Completable
    fun deleteCity(city: City): Completable
    fun fetchCities(): Single<Result<List<City>>>
    fun deleteAllCities(): Completable
    fun getCurrentCity(): Single<Result<City>>
    fun getPlaceId(city: String): Single<Result<String>>
}
