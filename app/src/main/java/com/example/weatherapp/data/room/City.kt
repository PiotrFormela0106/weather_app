package com.example.weatherapp.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class City(val city: String) {
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0//rename to cityId and move in constructor
}