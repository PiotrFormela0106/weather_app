package com.example.weather_data.models

import com.google.gson.annotations.SerializedName

data class AirPollution(
    @SerializedName("coord") val coordinates: Coordinates,
    val list: List<AirPollutionItem>
)

data class ForecastWeather(val city: City, val list: List<ForecastItem>)
data class CurrentWeather(
    @SerializedName("name") val cityName: String,
    val main: MainInfo,
    @SerializedName("coord") val coordinates: Coordinates,
    val weather: List<Weather>,
    val clouds: Clouds,
    val sys: Sun,
    val wind: Wind,
    @SerializedName("cod") val response: Int
)

data class ForecastItem(
    val main: MainInfo,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    @SerializedName("dt_txt") val date: String,
    val rain: Rain? = null,
    @SerializedName("dt") val dateLong: Long
)

data class AirPollutionItem(
    val main: AirQualityIndex,
    val components: Components,
    val dt: Int
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

data class AirQualityIndex(
    val aqi: Int
)

data class MainInfo(
    val temp: Double,
    @SerializedName("feels_Like") val feelsLike: Double,
    val pressure: Long,
    val humidity: Long,
)

data class Rain(
    @SerializedName("3h") val the3H: Double
)

data class City(
    val name: String,
    @SerializedName("coord") val coordinates: Coordinates,
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
