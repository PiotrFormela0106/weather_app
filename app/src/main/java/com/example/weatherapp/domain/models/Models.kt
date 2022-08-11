package com.example.weatherapp.domain.models

import com.google.gson.annotations.SerializedName

data class ForecastWeather(var city: City, val list: List<ListElement>)

data class CurrentWeather(
    @SerializedName("name") var cityName: String,//remove this from domain model, you don't use serialization
    val main: Main,
    val coord: Coord,// rename to coordinates
    val weather: List<Weather>,
    val clouds: Clouds,
    val sys: Sys,// rename to something better
    val wind: Wind
)

data class ListElement (//rename to ForecastItem
    val dt: Long,// I don't know meaning of this field. if it will not be used then it can be removed
    val main: MainClass,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Double,
    val sys: SysClass,
    @SerializedName("dt_txt") val dtTxt: String,//remove this from domain model, you don't use serialization
    val rain: Rain? = null
)

data class MainClass (//rename to MainInfo
    val temp: Double,
    @SerializedName("feels_Like") val feelsLike: Double,//remove this from domain model, you don't use serialization
    @SerializedName("temp_min")val tempMin: Double,//remove this from domain model, you don't use serialization
    @SerializedName("temp_max")val tempMax: Double,//remove this from domain model, you don't use serialization
    val pressure: Long,
    @SerializedName("sea_level")val seaLevel: Long,
    @SerializedName("grnd_level")val grindLevel: Long,
    val humidity: Long,
    @SerializedName("temp_kf")val tempKf: Double
)

data class Rain (
    val the3H: Double
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
data class SysClass(// I don't know meaning of this field. if it will not be used then it can be removed
    val pod: String// if it will be used them rename to clear title
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