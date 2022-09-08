package com.example.weatherapp.data.repo

import com.example.weatherapp.BuildConfig.API_KEY
import com.example.weatherapp.data.Cache
import com.example.weatherapp.data.CacheKey
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.getCityCacheKey
import com.example.weatherapp.data.getLocationCacheKey
import com.example.weatherapp.data.mappers.AirPollutionData
import com.example.weatherapp.data.mappers.CurrentWeatherData
import com.example.weatherapp.data.mappers.ForecastWeatherData
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
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    retrofitClient: RetrofitClient,
    val storageRepository: StorageRepository
) : WeatherRepository {
    private val api = retrofitClient.api
    private val cache = Cache()
    override fun getCurrentWeather(): Single<Result<CurrentWeather>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> {
                val cacheKey = getCityCacheKey(getCityParam(), getUnitsParam(), getLanguageParam())
                val cachedValue = cache.getCurrentWeather(cacheKey)
                if (cachedValue != null) {
                    return Single.just(Result.withValue(cachedValue))
                }
                api.getCurrentWeatherForCity(
                    cityName = getCityParam(),
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                ).compose(mapCurrentWeatherResponse(cacheKey))
            }
            LocationMethod.Location -> {
                val cacheKey = getLocationCacheKey(
                    getCoordinatesParam().first,
                    getCoordinatesParam().second,
                    getUnitsParam(),
                    getLanguageParam()
                )
                val cachedValue = cache.getCurrentWeather(cacheKey)
                if (cachedValue != null) {
                    return Single.just(Result.withValue(cachedValue))
                }
                api.getCurrentWeatherForLocation(
                    lat = getCoordinatesParam().first,
                    lon = getCoordinatesParam().second,
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                ).compose(mapCurrentWeatherResponse(cacheKey))
            }
        }
    }

    override fun getForecastWeather(): Single<Result<ForecastWeather>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> {
                val cacheKey = getCityCacheKey(getCityParam(), getUnitsParam(), getLanguageParam())
                val cachedValue = cache.getForecast(cacheKey)
                if (cachedValue != null) {
                    return Single.just(Result.withValue(cachedValue))
                }
                api.getForecastForCity(
                    cityName = getCityParam(),
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                ).compose(mapForecastWeatherResponse(cacheKey))
            }
            LocationMethod.Location -> {
                val cacheKey = getLocationCacheKey(
                    getCoordinatesParam().first,
                    getCoordinatesParam().second,
                    getUnitsParam(),
                    getLanguageParam()
                )
                val cachedValue = cache.getForecast(cacheKey)
                if (cachedValue != null) {
                    return Single.just(Result.withValue(cachedValue))
                }
                api.getForecastForLocation(
                    lat = getCoordinatesParam().first,
                    lon = getCoordinatesParam().second,
                    apikey = API_KEY,
                    units = getUnitsParam(),
                    lang = getLanguageParam()
                ).compose(mapForecastWeatherResponse(cacheKey))
            }
        }
    }

    override fun getAirPollution(lat: Double, lon: Double): Single<Result<AirPollution>> {
        return api.getAirPollution(lat = lat, lon = lon, apikey = API_KEY)
            .compose(mapAirPollutionResponse())
    }

    private fun mapCurrentWeatherResponse(cacheKey: CacheKey):
        SingleTransformer<CurrentWeatherData, Result<CurrentWeather>> {
        return SingleTransformer { upstream ->
            upstream
                .map { it.toDomain() }
                .doOnSuccess { cache.cache(cacheKey, it) }
                .map { Result.withValue(it) }
                .onErrorReturn { it.toResultError() }
        }
    }

    private fun mapForecastWeatherResponse(cacheKey: CacheKey):
        SingleTransformer<ForecastWeatherData, Result<ForecastWeather>> {
        return SingleTransformer { upstream ->
            upstream
                .map { it.toDomain() }
                .doOnSuccess { cache.cache(cacheKey, it) }
                .map { Result.withValue(it) }
                .onErrorReturn { it.toResultError() }
        }
    }

    private fun mapAirPollutionResponse(): SingleTransformer<AirPollutionData, Result<AirPollution>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn { it.toResultError() }
        }
    }

    private fun <T> Throwable.toResultError(): Result<T> {
        val error = this.toError()
        return Result.withError<T>(error)
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
    }
}
