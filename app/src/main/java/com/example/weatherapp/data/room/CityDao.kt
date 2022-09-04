package com.example.weatherapp.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface CityDao {
    @Insert
    fun insert(city: City): Completable
    @Delete
    fun delete(city: City): Completable
    @Query("SELECT * FROM city_table")
    fun getAllCities(): Single<List<City>>
    @Query("DELETE FROM city_table")
    fun deleteAllCities(): Completable
    @Query("SELECT placeId FROM city_table WHERE city = :cityName")
    fun getPlaceId(cityName: String): Single<String>
}
