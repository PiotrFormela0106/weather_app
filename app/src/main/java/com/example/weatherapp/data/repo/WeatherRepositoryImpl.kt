package com.example.weatherapp.data.repo

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.mappers.*
import com.example.weatherapp.domain.models.*
import com.example.weatherapp.domain.repo.WeatherRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import com.example.weatherapp.domain.Result
import javax.inject.Inject
import com.example.weatherapp.domain.toError
import com.example.weatherapp.BuildConfig.API_KEY
import com.example.weatherapp.data.Cache
import com.example.weatherapp.data.CacheKey
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.data.getCityCacheKey
import com.example.weatherapp.data.getLocationCacheKey

private const val LANG_PL = "pl"

class WeatherRepositoryImpl @Inject constructor(
    retrofitClient: RetrofitClient,
    val storageRepository: StorageRepository
) : WeatherRepository {
    private val api = retrofitClient.api
    private val cache = Cache()
    override fun getCurrentWeather(
        city: String?
    ): Single<Result<CurrentWeather>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> api.getCurrentWeatherForCity(
                cityName = city,
                apikey = API_KEY,
                lang = LANG_PL,
                units = getUnitsParam()
            ).compose(mapCurrentWeatherResponse())
            LocationMethod.Location -> api.getCurrentWeatherForLocation(
                lat = getLatitudeParam(),
                lon = getLongitudeParam(),
                apikey = API_KEY,
                units = getUnitsParam()
            ).compose(mapCurrentWeatherResponse())
        }

    }

    override fun getForecastWeather(
    ): Single<Result<ForecastWeather>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> {
                val cacheKey = getCityCacheKey(getCityParam(), getUnitsParam(), LANG_PL)
                val cachedValue = cache.getForecast(cacheKey)
                if (cachedValue != null) {
                    return Single.just(Result.withValue(cachedValue))
                }
                api.getForecastForCity(
                    cityName = getCityParam(),
                    apikey = API_KEY,
                    lang = LANG_PL,
                    units = getUnitsParam()
                ).compose(mapForecastWeatherResponse(cacheKey))
            }
            LocationMethod.Location -> {
                val cacheKey = getLocationCacheKey(
                    getLatitudeParam(),
                    getLongitudeParam(),
                    getUnitsParam(),
                    LANG_PL
                )
                val cachedValue = cache.getForecast(cacheKey)
                if (cachedValue != null) {
                    return Single.just(Result.withValue(cachedValue))
                }
                api.getForecastForLocation(
                    lat = getLatitudeParam(),
                    lon = getLongitudeParam(),
                    apikey = API_KEY,
                    units = getUnitsParam(),
                    lang = LANG_PL
                ).compose(mapForecastWeatherResponse(cacheKey))
            }
        }
    }

    override fun getAirPollution(lat: Double, lon: Double): Single<Result<AirPollution>> {
        return api.getAirPollution(lat = lat, lon = lon, apikey = API_KEY)
            .compose(mapAirPollutionResponse())
    }

    private fun mapCurrentWeatherResponse():
            SingleTransformer<CurrentWeatherData, Result<CurrentWeather>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
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
    private fun getLatitudeParam(): Double = storageRepository.getLatitude()
    private fun getLongitudeParam(): Double = storageRepository.getLongitude()
    private fun getCityParam(): String = storageRepository.getCity()

}

private fun Units.toQueryParam(): String {
    return when (this) {
        Units.Metric -> "metric"
        Units.NotMetric -> "standard"
    }
}