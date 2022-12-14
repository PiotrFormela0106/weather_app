package com.example.weatherapp.data.repo

import com.example.weatherapp.BuildConfig.API_KEY
import com.example.weatherapp.data.api.RetrofitClient
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
    override fun getCurrentWeather(): Single<Result<CurrentWeather>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> {
                api.getCurrentWeatherForCity(
                    cityName = getCityParam(),
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                ).compose(mapCurrentWeatherResponse())
            }
            LocationMethod.Location, LocationMethod.Map -> {
                api.getCurrentWeatherForLocation(
                    lat = getCoordinatesParam().first,
                    lon = getCoordinatesParam().second,
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                ).compose(mapCurrentWeatherResponse())
            }
        }
    }

    override fun getForecastWeather(): Single<Result<ForecastWeather>> {
        return when (storageRepository.getLocationMethod()) {
            LocationMethod.City -> {
                api.getForecastForCity(
                    cityName = getCityParam(),
                    apikey = API_KEY,
                    lang = getLanguageParam(),
                    units = getUnitsParam()
                ).compose(mapForecastWeatherResponse())
            }
            LocationMethod.Location, LocationMethod.Map -> {
                api.getForecastForLocation(
                    lat = getCoordinatesParam().first,
                    lon = getCoordinatesParam().second,
                    apikey = API_KEY,
                    units = getUnitsParam(),
                    lang = getLanguageParam()
                ).compose(mapForecastWeatherResponse())
            }
        }
    }

    override fun getAirPollution(): Single<Result<AirPollution>> {
        return api.getAirPollution(
            lat = getCoordinatesParam().first,
            lon = getCoordinatesParam().second,
            apikey = API_KEY
        )
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

    private fun mapForecastWeatherResponse():
            SingleTransformer<ForecastWeatherData, Result<ForecastWeather>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
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
        Language.DE -> "de"
    }
}
