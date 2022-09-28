package com.example.weatherapp.data.repo

import com.example.weatherapp.BuildConfig.API_KEY
import com.example.weatherapp.data.Cache
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.getCityCacheKey
import com.example.weatherapp.data.getLocationCacheKey
import com.example.weatherapp.data.mappers.toDomain
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.Language
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.domain.toError
import java.lang.Exception
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    retrofitClient: RetrofitClient,
    val storageRepository: StorageRepository
) : WeatherRepository {
    private val api = retrofitClient.api
    private val cache = Cache()
    override suspend fun getCurrentWeather(): Result<CurrentWeather> {
        return try {
            when (storageRepository.getLocationMethod()) {
                LocationMethod.City -> {
                    val cacheKey = getCityCacheKey(getCityParam(), getUnitsParam(), getLanguageParam())
                    val cachedValue = cache.getCurrentWeather(cacheKey)
                    if (cachedValue != null) {
                        return Result.withValue(cachedValue)
                    }
                    val result = api.getCurrentWeatherForCity(
                        cityName = getCityParam(),
                        apikey = API_KEY,
                        lang = getLanguageParam(),
                        units = getUnitsParam()
                    )
                    cache.cache(cacheKey, result.toDomain())
                    Result.withValue(result.toDomain())
                }
                LocationMethod.Location, LocationMethod.Map -> {
                    val cacheKey = getLocationCacheKey(
                        getCoordinatesParam().first,
                        getCoordinatesParam().second,
                        getUnitsParam(),
                        getLanguageParam()
                    )
                    val cachedValue = cache.getCurrentWeather(cacheKey)
                    if (cachedValue != null) {
                        return Result.withValue(cachedValue)
                    }
                    val result = api.getCurrentWeatherForLocation(
                        lat = getCoordinatesParam().first,
                        lon = getCoordinatesParam().second,
                        apikey = API_KEY,
                        lang = getLanguageParam(),
                        units = getUnitsParam()
                    )
                    cache.cache(cacheKey, result.toDomain())
                    Result.withValue(result.toDomain())
                }
            }
        } catch (ex: Exception) {
            ex.toResultError()
        }
    }

    override suspend fun getForecastWeather(): Result<ForecastWeather> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> {
                val cacheKey = getCityCacheKey(getCityParam(), getUnitsParam(), getLanguageParam())
                val cachedValue = cache.getForecast(cacheKey)
                if (cachedValue != null) {
                    return Result.withValue(cachedValue)
                }
                val result = api.getForecastForCity(
                    cityName = getCityParam(),
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                )
                cache.cache(cacheKey, result.toDomain())
                Result.withValue(result.toDomain())
            }
            LocationMethod.Location, LocationMethod.Map -> {
                val cacheKey = getLocationCacheKey(
                    getCoordinatesParam().first,
                    getCoordinatesParam().second,
                    getUnitsParam(),
                    getLanguageParam()
                )
                val cachedValue = cache.getForecast(cacheKey)
                if (cachedValue != null) {
                    return Result.withValue(cachedValue)
                }
                val result = api.getForecastForLocation(
                    lat = getCoordinatesParam().first,
                    lon = getCoordinatesParam().second,
                    apikey = API_KEY,
                    units = getUnitsParam(),
                    lang = getLanguageParam()
                )
                cache.cache(cacheKey, result.toDomain())
                Result.withValue(result.toDomain())
            }
        }
    }

    override suspend fun getAirPollution(): Result<AirPollution> {
        return try {
            val result = api.getAirPollution(
                lat = getCoordinatesParam().first,
                lon = getCoordinatesParam().second,
                apikey = API_KEY
            )
            Result.withValue(result.toDomain())
        } catch (ex: Exception) {
            ex.toResultError()
        }
    }

    private fun <T> Throwable.toResultError(): Result<T> {
        val error = this.toError()
        return Result.withError(error)
    }

    private fun getUnitsParam(): String = storageRepository.getUnits().toQueryParam()
    private fun getCityParam(): String = storageRepository.getCity()
    private fun getCoordinatesParam(): Pair<Double, Double> = storageRepository.getCoordinates()
    private fun getLanguageParam(): String = storageRepository.getLanguage().toQueryParam()
}

private fun Units.toQueryParam(): String {
    return when (this) {
        Units.Metric -> "metric"
        Units.NotMetric -> "standard"
    }
}

private fun Language.toQueryParam(): String {
    return when (this) {
        Language.ENG -> "eng"
        Language.PL -> "pl"
        Language.DE -> "de"
    }
}
