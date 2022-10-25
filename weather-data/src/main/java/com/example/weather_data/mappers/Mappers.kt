package com.example.weather_data.mappers

import com.example.weather_data.models.AirPollution
import com.example.weather_data.models.AirPollutionItem
import com.example.weather_data.models.AirQualityIndex
import com.example.weather_data.models.City
import com.example.weather_data.models.Clouds
import com.example.weather_data.models.Components
import com.example.weather_data.models.Coordinates
import com.example.weather_data.models.CurrentWeather
import com.example.weather_data.models.ForecastItem
import com.example.weather_data.models.ForecastWeather
import com.example.weather_data.models.MainInfo
import com.example.weather_data.models.Rain
import com.example.weather_data.models.Sun
import com.example.weather_data.models.Weather
import com.example.weather_data.models.Wind

typealias CurrentWeatherDomain = com.example.weather_domain.models.CurrentWeather
typealias ForecastWeatherDomain = com.example.weather_domain.models.ForecastWeather
typealias MainInfoDomain = com.example.weather_domain.models.MainInfo
typealias RainDomain = com.example.weather_domain.models.Rain
typealias CityDomain = com.example.weather_domain.models.City
typealias WeatherDomain = com.example.weather_domain.models.Weather
typealias CloudsDomain = com.example.weather_domain.models.Clouds
typealias SunDomain = com.example.weather_domain.models.Sun
typealias WindDomain = com.example.weather_domain.models.Wind
typealias CoordinatesDomain = com.example.weather_domain.models.Coordinates
typealias ForecastItemDomain = com.example.weather_domain.models.ForecastItem
typealias AirPollutionDomain = com.example.weather_domain.models.AirPollution
typealias AirQualityIndexDomain = com.example.weather_domain.models.AirQualityIndex
typealias AirPollutionItemDomain = com.example.weather_domain.models.AirPollutionItem
typealias ComponentsDomain = com.example.weather_domain.models.Components

typealias CurrentWeatherData = CurrentWeather
typealias ForecastWeatherData = ForecastWeather
typealias MainInfoData = MainInfo
typealias RainData = Rain
typealias CityData = City
typealias WeatherData = Weather
typealias CloudsData = Clouds
typealias SunData = Sun
typealias WindData = Wind
typealias CoordinatesData = Coordinates
typealias ForecastItemData = ForecastItem
typealias AirPollutionData = AirPollution
typealias AirQualityIndexData = AirQualityIndex
typealias AirPollutionItemData = AirPollutionItem
typealias ComponentsData = Components

fun AirPollutionData.toDomain(): AirPollutionDomain =
    com.example.weather_data.mappers.AirPollutionDomain(
        coordinates = coordinates.toDomain(),
        list = list.map { it.toDomain() }
    )

fun AirQualityIndexData.toDomain(): AirQualityIndexDomain =
    com.example.weather_data.mappers.AirQualityIndexDomain(
        aqi = aqi
    )

fun AirPollutionItemData.toDomain(): AirPollutionItemDomain =
    com.example.weather_data.mappers.AirPollutionItemDomain(
        main = main.toDomain(),
        components = components.toDomain(),
        dt = dt
    )

fun ComponentsData.toDomain(): ComponentsDomain = com.example.weather_data.mappers.ComponentsDomain(
    co = co,
    no = no,
    no2 = no2,
    o3 = o3,
    so2 = so2,
    pm2_5 = pm2_5,
    pm10 = pm10,
    nh3 = nh3
)

fun CoordinatesData.toDomain(): CoordinatesDomain =
    com.example.weather_data.mappers.CoordinatesDomain(
        lon = lon,
        lat = lat
    )

fun WeatherData.toDomain(): WeatherDomain = com.example.weather_data.mappers.WeatherDomain(
    main = main,
    description = description,
    icon = icon
)

fun CloudsData.toDomain(): CloudsDomain = com.example.weather_data.mappers.CloudsDomain(
    all = all
)

fun SunData.toDomain(): SunDomain = com.example.weather_data.mappers.SunDomain(
    sunrise = sunrise,
    sunset = sunset,
)

fun WindData.toDomain(): WindDomain = com.example.weather_data.mappers.WindDomain(
    speed = speed
)

fun CurrentWeatherData.toDomain(): CurrentWeatherDomain =
    com.example.weather_data.mappers.CurrentWeatherDomain(
        cityName = cityName,
        main = main.toDomain(),
        coordinates = coordinates.toDomain(),
        weather = weather.map { it.toDomain() },
        clouds = clouds.toDomain(),
        sys = sys.toDomain(),
        wind = wind.toDomain(),
        response = response
    )

fun CityData.toDomain(): CityDomain = com.example.weather_data.mappers.CityDomain(
    name = name,
    coordinates = coordinates.toDomain(),
    country = country,
    sunrise = sunrise,
    sunset = sunset
)

fun MainInfoData.toDomain(): MainInfoDomain = com.example.weather_data.mappers.MainInfoDomain(
    temp = temp,
    feelsLike = feelsLike,
    pressure = pressure,
    humidity = humidity
)

fun RainData.toDomain(): RainDomain = com.example.weather_data.mappers.RainDomain(
    the3H = the3H
)

fun ForecastItemData.toDomain(): ForecastItemDomain =
    com.example.weather_data.mappers.ForecastItemDomain(
        main = main.toDomain(),
        weather = weather.map { it.toDomain() },
        clouds = clouds.toDomain(),
        wind = wind.toDomain(),
        date = date,
        rain = rain?.toDomain(),
        dateLong = dateLong
    )

fun ForecastWeatherData.toDomain(): ForecastWeatherDomain =
    com.example.weather_data.mappers.ForecastWeatherDomain(
        city = city.toDomain(),
        list = list.map { it.toDomain() }
    )
