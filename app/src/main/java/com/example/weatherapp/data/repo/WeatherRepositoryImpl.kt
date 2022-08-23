package com.example.weatherapp.data.repo

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.mappers.*
import com.example.weatherapp.domain.CityError
import com.example.weatherapp.domain.models.*
import com.example.weatherapp.domain.repo.WeatherRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import com.example.weatherapp.domain.Result
import javax.inject.Inject
import com.example.weatherapp.domain.Error
import com.example.weatherapp.domain.toError
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.BuildConfig.API_KEY
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.domain.repo.StorageRepository

private const val LANG_PL = "pl"
private const val LANG_ENG = "eng"
private const val METRIC = "METRIC"

class WeatherRepositoryImpl @Inject constructor(
    retrofitClient: RetrofitClient,
    val storageRepository: StorageRepository
) : WeatherRepository {
    enum class LocationMethod {
        City, Location;

        companion object {
            fun toLocationMethod(methodString: String): LocationMethod {
                return valueOf(methodString)
            }
        }
    }

    private val api = retrofitClient.api
    override fun getCurrentWeather(
        city: String?,
        lat: Double?,
        lon: Double?
    ): Single<Result<CurrentWeatherDomain>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> api.getCurrentWeatherForCity(
                cityName = city,
                apikey = API_KEY,
                lang = LANG_PL,
                units = getUnitsParam()
            ).compose(mapCurrentWeatherResponse())
            LocationMethod.Location -> api.getCurrentWeatherForLocation(
                lat = lat,
                lon = lon,
                apikey = API_KEY,
                units = getUnitsParam()
            ).compose(mapCurrentWeatherResponse())
        }

    }

    override fun getForecastWeather(
        city: String?,
        lat: Double?,
        lon: Double?
    ): Single<Result<ForecastWeatherDomain>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> api.getForecastForCity(
                cityName = city,
                apikey = API_KEY,
                lang = LANG_PL,
                units = getUnitsParam()
            ).compose(mapForecastWeatherResponse())
            LocationMethod.Location -> api.getForecastForLocation(
                lat = lat,
                lon = lon,
                apikey = API_KEY,
                units = getUnitsParam()
            ).compose(mapForecastWeatherResponse())
        }
    }

    override fun getAirPollution(lat: Double, lon: Double):Single<Result<AirPollutionDomain>> {
        return api.getAirPollution(lat = lat, lon = lon, apikey = API_KEY)
            .compose(mapAirPollutionResponse())
    }

    private fun mapCurrentWeatherResponse():
            SingleTransformer<CurrentWeatherData, Result<CurrentWeatherDomain>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn { it.toResultError() }
            //.onErrorReturn { Result.withError(Error(it)) }
        }
    }

    private fun mapForecastWeatherResponse():
            SingleTransformer<ForecastWeatherData, Result<ForecastWeatherDomain>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn { it.toResultError() }
            //.onErrorReturn { Result.withError(Error(it)) }
        }
    }

    private fun mapAirPollutionResponse():SingleTransformer<AirPollutionData, Result<AirPollutionDomain>>{
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
}

private fun Units.toQueryParam(): String {
    return when (this) {
        Units.Metric -> "metric"
        Units.NotMetric -> "standard"
    }
}