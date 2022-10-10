package com.example.city_data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CityDao {
    @Insert
    suspend fun insert(city: City)
    @Delete
    suspend fun delete(city: City)
    @Query("SELECT * FROM city_table")
    suspend fun getAllCities(): List<City>
    @Query("DELETE FROM city_table")
    suspend fun deleteAllCities()
    @Query("SELECT photoId FROM city_table WHERE city = :cityName")
    suspend fun getPhotoId(cityName: String): String
}
