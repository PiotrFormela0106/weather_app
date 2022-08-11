package com.example.weatherapp.data.models

import com.google.gson.annotations.SerializedName

data class ForecastWeather(var city: City, val list: List<ListElement>)

data class CurrentWeather(
    @SerializedName("name") var cityName: String,
    val main: Main,
    val coord: Coord,
    val weather: List<Weather>,
    val clouds: Clouds,
    val sys: Sys,
    val wind: Wind
)

data class ListElement (
    val dt: Long,
    val main: MainClass,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Double,
    val sys: SysClass,
    @SerializedName("dt_txt") val dtTxt: String,
    val rain: Rain? = null
)

data class MainClass (
    val temp: Double,
    @SerializedName("feels_Like") val feelsLike: Double,
    @SerializedName("temp_min")val tempMin: Double,
    @SerializedName("temp_max")val tempMax: Double,
    val pressure: Long,
    @SerializedName("sea_level")val seaLevel: Long,
    @SerializedName("grnd_level")val grindLevel: Long,
    val humidity: Long,
    @SerializedName("temp_kf")val tempKf: Double
)

data class Rain (
    @SerializedName("3h") val the3H: Double
)

data class City (
    val id: Long,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Long,
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long
)

data class Weather(
    //val id: Long,
    val main: String,
    val description: String,
    val icon: String
)
//current-weather
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

data class SysClass(
    val pod: String
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