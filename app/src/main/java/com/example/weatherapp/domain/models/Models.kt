package com.example.weatherapp.domain.models

data class ForecastWeather(var city: City, val list: List<ForecastItem>)

data class CurrentWeather(
    var cityName: String,
    val main: MainInfo,
    val coordinates: Coordinates,
    val weather: List<Weather>,
    val clouds: Clouds,
    val sys: Sun,
    val wind: Wind
)

data class ForecastItem (
    val main: MainInfo,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val date: String,
    val rain: Rain? = null
)

data class MainInfo (
    val temp: Double,
    val feelsLike: Double,
    val pressure: Long,
    val humidity: Long,
)

data class Rain (
    val the3H: Double
)

data class City (
    val name: String,
    val coordinates: Coordinates,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds (
    val all: Long
)

data class Sun (
    val sunrise: Long,
    val sunset: Long
)
data class Wind (
    val speed: Double
)

data class Coordinates(
    val lon: Double,
    val lat: Double
)