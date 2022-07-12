package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class WeatherClass(
    @SerializedName("name") val cityName: String,
    val main: Main,
    val coord: Coord,
    val weather: List<Weather>,
    val clouds: Clouds,
    val sys: Sys,
    val wind: Wind
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double, val pressure: Long,
    val humidity: Long,
    @SerializedName("sea_level") val seaLevel: Long,
    @SerializedName("grnd_level") val grindLevel: Long

)
data class Clouds (
    val all: Long
)

data class Sys (
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Wind (
    val speed: Double,
    val deg: Long,
    val gust: Double
)

data class Coord(
    val lon: Double,
    val lat: Double
)