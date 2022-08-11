package com.example.weatherapp.data.mappers

typealias CurrentWeatherDomain = com.example.weatherapp.domain.models.CurrentWeather
typealias ForecastWeatherDomain = com.example.weatherapp.domain.models.ForecastWeather
typealias MainClassDomain = com.example.weatherapp.domain.models.MainClass
typealias RainDomain = com.example.weatherapp.domain.models.Rain
typealias CityDomain = com.example.weatherapp.domain.models.City
typealias WeatherDomain = com.example.weatherapp.domain.models.Weather
typealias MainDomain = com.example.weatherapp.domain.models.Main
typealias CloudsDomain = com.example.weatherapp.domain.models.Clouds
typealias SysDomain = com.example.weatherapp.domain.models.Sys
typealias SysClassDomain = com.example.weatherapp.domain.models.SysClass
typealias WindDomain = com.example.weatherapp.domain.models.Wind
typealias CoordDomain = com.example.weatherapp.domain.models.Coord
typealias ListElementDomain = com.example.weatherapp.domain.models.ListElement

typealias CurrentWeatherData = com.example.weatherapp.data.models.CurrentWeather
typealias ForecastWeatherData = com.example.weatherapp.data.models.ForecastWeather
typealias MainClassData = com.example.weatherapp.data.models.MainClass
typealias RainData = com.example.weatherapp.data.models.Rain
typealias CityData = com.example.weatherapp.data.models.City
typealias WeatherData = com.example.weatherapp.data.models.Weather
typealias MainData = com.example.weatherapp.data.models.Main
typealias CloudsData = com.example.weatherapp.data.models.Clouds
typealias SysData = com.example.weatherapp.data.models.Sys
typealias SysClassData = com.example.weatherapp.data.models.SysClass
typealias WindData = com.example.weatherapp.data.models.Wind
typealias CoordData = com.example.weatherapp.data.models.Coord
typealias ListElementData = com.example.weatherapp.data.models.ListElement

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

fun CityData.toDomain(): CityDomain = CityDomain(
    id = id,
    name = name,
    coord = coord.toDomain(),
    country = country,
    population = population,
    timezone = timezone,
    sunrise = sunrise,
    sunset = sunset
)

fun MainClassData.toDomain(): MainClassDomain = MainClassDomain(
    temp = temp,
    feelsLike = feelsLike,
    tempMin = tempMin,
    tempMax = tempMax,
    pressure = pressure,
    seaLevel = seaLevel,
    grindLevel = grindLevel,
    humidity = humidity,
    tempKf = tempKf
)

fun RainData.toDomain(): RainDomain = RainDomain(
    the3H = the3H
)

fun SysClassData.toDomain(): SysClassDomain = SysClassDomain(
    pod = pod
)

fun ListElementData.toDomain(): ListElementDomain = ListElementDomain(
    dt = dt,
    main = main.toDomain(),
    weather = weather.map{it.toDomain()},
    clouds = clouds.toDomain(),
    wind = wind.toDomain(),
    visibility = visibility,
    pop = pop,
    sys = sys.toDomain(),
    dtTxt = dtTxt,
    rain = rain?.toDomain()
)

fun ForecastWeatherData.toDomain(): ForecastWeatherDomain = ForecastWeatherDomain(
    city = city.toDomain(),
    list = list.map{it.toDomain()}
)


