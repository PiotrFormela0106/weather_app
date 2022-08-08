package com.example.weatherapp.data.mappers

typealias CurrentWeatherDomain = com.example.weatherapp.domain.models.CurrentWeather
typealias ForecastWeatherDomain = com.example.weatherapp.domain.models.ForecastWeather
typealias ListElementDomain = com.example.weatherapp.domain.models.ListElement
typealias MainClassDomain = com.example.weatherapp.domain.models.MainClass
typealias RainDomain = com.example.weatherapp.domain.models.Rain
typealias CityDomain = com.example.weatherapp.domain.models.City
typealias WeatherDomain = com.example.weatherapp.domain.models.Weather
typealias MainDomain = com.example.weatherapp.domain.models.Main
typealias CloudsDomain = com.example.weatherapp.domain.models.Clouds
typealias SysDomain = com.example.weatherapp.domain.models.Sys
typealias WindDomain = com.example.weatherapp.domain.models.Wind
typealias CoordDomain = com.example.weatherapp.domain.models.Coord

typealias CurrentWeatherData = com.example.weatherapp.data.models.CurrentWeather
typealias ForecastWeatherData = com.example.weatherapp.data.models.ForecastWeather
typealias ListElementData = com.example.weatherapp.data.models.ListElement
typealias MainClassData = com.example.weatherapp.data.models.MainClass
typealias RainData = com.example.weatherapp.data.models.Rain
typealias CityData = com.example.weatherapp.data.models.City
typealias WeatherData = com.example.weatherapp.data.models.Weather
typealias MainData = com.example.weatherapp.data.models.Main
typealias CloudsData = com.example.weatherapp.data.models.Clouds
typealias SysData = com.example.weatherapp.data.models.Sys
typealias WindData = com.example.weatherapp.data.models.Wind
typealias CoordData = com.example.weatherapp.data.models.Coord

fun MainData.toDomain(): MainDomain = MainDomain(
    temp = temp,
    feelsLike = feelsLike,
    tempMin = tempMin,
    tempMax = tempMax,
    pressure = pressure,
    humidity = humidity,
    seaLevel = seaLevel,
    grindLevel = grindLevel
)

fun CoordData.toDomain(): CoordDomain = CoordDomain(
    lon = lon,
    lat = lat
)

fun WeatherData.toDomain(): WeatherDomain = WeatherDomain(
    id = id,
    main = main,
    description = description,
    icon = icon
)

fun CloudsData.toDomain(): CloudsDomain = CloudsDomain(
    all = all
)

fun SysData.toDomain(): SysDomain = SysDomain(
    type = type,
    id = id,
    country = country,
    sunrise = sunrise,
    sunset = sunset,
)

fun WindData.toDomain(): WindDomain = WindDomain(
    speed = speed,
    deg = deg,
    gust = gust
)

fun CurrentWeatherData.toDomain(): CurrentWeatherDomain = CurrentWeatherDomain(
    cityName = cityName,
    main = main.toDomain(),
    coord = coord.toDomain(),
    weather = weather.map{it.toDomain()},
    clouds = clouds.toDomain(),
    sys = sys.toDomain(),
    wind = wind.toDomain()
)


