package com.example.weatherapp.data.mappers

import com.example.weatherapp.domain.models.Language
import com.example.weatherapp.domain.models.Units

typealias CurrentWeatherDomain = com.example.weatherapp.domain.models.CurrentWeather
typealias ForecastWeatherDomain = com.example.weatherapp.domain.models.ForecastWeather
typealias MainInfoDomain = com.example.weatherapp.domain.models.MainInfo
typealias RainDomain = com.example.weatherapp.domain.models.Rain
typealias CityDomain = com.example.weatherapp.domain.models.City
typealias WeatherDomain = com.example.weatherapp.domain.models.Weather
typealias CloudsDomain = com.example.weatherapp.domain.models.Clouds
typealias SunDomain = com.example.weatherapp.domain.models.Sun
typealias WindDomain = com.example.weatherapp.domain.models.Wind
typealias CoordinatesDomain = com.example.weatherapp.domain.models.Coordinates
typealias ForecastItemDomain = com.example.weatherapp.domain.models.ForecastItem
typealias AirPollutionDomain = com.example.weatherapp.domain.models.AirPollution
typealias AirQualityIndexDomain = com.example.weatherapp.domain.models.AirQualityIndex
typealias AirPollutionItemDomain = com.example.weatherapp.domain.models.AirPollutionItem
typealias ComponentsDomain = com.example.weatherapp.domain.models.Components

typealias CurrentWeatherData = com.example.weatherapp.data.models.CurrentWeather
typealias ForecastWeatherData = com.example.weatherapp.data.models.ForecastWeather
typealias MainInfoData = com.example.weatherapp.data.models.MainInfo
typealias RainData = com.example.weatherapp.data.models.Rain
typealias CityData = com.example.weatherapp.data.models.City
typealias WeatherData = com.example.weatherapp.data.models.Weather
typealias CloudsData = com.example.weatherapp.data.models.Clouds
typealias SunData = com.example.weatherapp.data.models.Sun
typealias WindData = com.example.weatherapp.data.models.Wind
typealias CoordinatesData = com.example.weatherapp.data.models.Coordinates
typealias ForecastItemData = com.example.weatherapp.data.models.ForecastItem
typealias AirPollutionData = com.example.weatherapp.data.models.AirPollution
typealias AirQualityIndexData = com.example.weatherapp.data.models.AirQualityIndex
typealias AirPollutionItemData = com.example.weatherapp.data.models.AirPollutionItem
typealias ComponentsData = com.example.weatherapp.data.models.Components

fun AirPollutionData.toDomain(): AirPollutionDomain = AirPollutionDomain(
    coordinates = coordinates.toDomain(),
    list = list.map { it.toDomain() }
)

fun AirQualityIndexData.toDomain(): AirQualityIndexDomain = AirQualityIndexDomain(
    aqi = aqi
)

fun AirPollutionItemData.toDomain(): AirPollutionItemDomain = AirPollutionItemDomain(
    main = main.toDomain(),
    components = components.toDomain(),
    dt = dt
)

fun ComponentsData.toDomain(): ComponentsDomain = ComponentsDomain(
    co = co,
    no = no,
    no2 = no2,
    o3 = o3,
    so2 = so2,
    pm2_5 = pm2_5,
    pm10 = pm10,
    nh3 = nh3
)

fun CoordinatesData.toDomain(): CoordinatesDomain = CoordinatesDomain(
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

fun SunData.toDomain(): SunDomain = SunDomain(
    sunrise = sunrise,
    sunset = sunset,
)

fun WindData.toDomain(): WindDomain = WindDomain(
    speed = speed
)

fun CurrentWeatherData.toDomain(): CurrentWeatherDomain = CurrentWeatherDomain(
    cityName = cityName,
    main = main.toDomain(),
    coordinates = coordinates.toDomain(),
    weather = weather.map { it.toDomain() },
    clouds = clouds.toDomain(),
    sys = sys.toDomain(),
    wind = wind.toDomain(),
    response = response
)

fun CityData.toDomain(): CityDomain = CityDomain(
    name = name,
    coordinates = coordinates.toDomain(),
    country = country,
    sunrise = sunrise,
    sunset = sunset
)

fun MainInfoData.toDomain(): MainInfoDomain = MainInfoDomain(
    temp = temp,
    feelsLike = feelsLike,
    pressure = pressure,
    humidity = humidity
)

fun RainData.toDomain(): RainDomain = RainDomain(
    the3H = the3H
)

fun ForecastItemData.toDomain(): ForecastItemDomain = ForecastItemDomain(
    main = main.toDomain(),
    weather = weather.map { it.toDomain() },
    clouds = clouds.toDomain(),
    wind = wind.toDomain(),
    date = date,
    rain = rain?.toDomain()
)

fun ForecastWeatherData.toDomain(): ForecastWeatherDomain = ForecastWeatherDomain(
    city = city.toDomain(),
    list = list.map { it.toDomain() }
)

fun String.toUnits(): Units {
    return if (NOT_METRIC.equals(this, true)) Units.NotMetric
    else Units.Metric
}

fun Units.toData(): String {
    return when (this) {
        Units.Metric -> METRIC
        Units.NotMetric -> NOT_METRIC
    }
}
fun Language.toData(): String {
    return when (this) {
        Language.ENG -> ENG
        Language.PL -> PL
    }
}
fun String.toLanguage(): Language {
    return if (PL.equals(this, true)) Language.PL
    else Language.ENG
}

private const val METRIC = "Metric"
private const val NOT_METRIC = "Not metric"
private const val PL = "PL"
private const val ENG = "ENG"
