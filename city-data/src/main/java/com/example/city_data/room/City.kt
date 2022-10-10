package com.example.city_data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class City(
    @PrimaryKey(autoGenerate = true)
    val cityId: Int = 0,
    val city: String,
    val photoId: String
)
