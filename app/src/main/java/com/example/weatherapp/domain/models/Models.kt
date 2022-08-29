package com.example.weatherapp.domain.models

data class AirPollution(val coordinates: Coordinates, val list: List<AirPollutionItem>)
data class ForecastWeather(val city: City, val list: List<ForecastItem>)

data class CurrentWeather(
    val cityName: String,
    val main: MainInfo,
    val coordinates: Coordinates,
    val weather: List<Weather>,
    val clouds: Clouds,
    val sys: Sun,
    val wind: Wind,
    val response: Int
)

data class ForecastItem(
    val main: MainInfo,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val date: String,
    val rain: Rain? = null
)

data class AirPollutionItem(
    val main: AirQualityIndex,
    val components: Components,
    val dt: Int
)

data class AirQualityIndex(
    val aqi: Int
)

data class Components(
    var co: Double,
    var no: Double,
    var no2: Double,
    var o3: Double,
    var so2: Double,
    var pm2_5: Double,
    var pm10: Double,
    var nh3: Double
)

data class MainInfo(
    val temp: Double,
    val feelsLike: Double,
    val pressure: Long,
    val humidity: Long,
)

data class Rain(
    val the3H: Double
)

data class City(
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

data class Clouds(
    val all: Long
)

data class Sun(
    val sunrise: Long,
    val sunset: Long
)

data class Wind(
    val speed: Double
)

data class Coordinates(
    val lon: Double,
    val lat: Double
)

enum class Units { Metric, NotMetric }

enum class LocationMethod {
    City, Location;
}