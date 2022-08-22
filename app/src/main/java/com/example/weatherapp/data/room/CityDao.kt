package com.example.weatherapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import com.example.weatherapp.domain.Result


@Dao
interface CityDao {
    @Insert
    fun insert(city: City): Completable
    @Update
    fun update(city: City)
    @Delete
    fun delete(city: City): Completable
    @Query("SELECT * FROM city_table")
    fun getAllCities(): Single<List<City>>
    @Query("DELETE FROM city_table")
    fun deleteAllCities(): Completable
}