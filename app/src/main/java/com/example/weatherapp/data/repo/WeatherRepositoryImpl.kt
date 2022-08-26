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
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.domain.toError
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import javax.inject.Inject

private const val LANG_PL = "pl"
private const val LANG_ENG = "eng"
private const val METRIC = "METRIC"

class WeatherRepositoryImpl @Inject constructor(
    retrofitClient: RetrofitClient,
    val storageRepository: StorageRepository
) : WeatherRepository {
    enum class LocationMethod {//move this enum to domain.models
        City, Location;

        //you can remove this fun and whole companion object
        companion object {//it's better to use extension fun
            //like
            //private fun String.toLocationMethod()LocationMethod
            fun toLocationMethod(methodString: String): LocationMethod {
                return valueOf(methodString)//this is a place to exception when String is empty
            //use when()operator
            }
        }
    }

    private val api = retrofitClient.api
    override fun getCurrentWeather(
        city: String?,
        lat: Double?,
        lon: Double?
    ): Single<Result<CurrentWeather>> {
        return when (storageRepository.getLocationMethod()) {
            //if city is null, you will have exception. add the check and return Result.Error
            LocationMethod.City -> api.getCurrentWeatherForCity(
                cityName = city,
                apikey = API_KEY,
                lang = LANG_PL,
                units = getUnitsParam()
            ).compose(mapCurrentWeatherResponse())
//            if lat or lon are null you will have exception. check and return Result.Error
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
        lon: Double?,
    ): Single<Result<ForecastWeather>> {
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

    override fun getAirPollution(lat: Double, lon: Double):Single<Result<AirPollution>> {
        return api.getAirPollution(lat = lat, lon = lon, apikey = API_KEY)
            .compose(mapAirPollutionResponse())
    }

    private fun mapCurrentWeatherResponse():
            SingleTransformer<CurrentWeatherData, Result<CurrentWeather>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn { it.toResultError() }
            //.onErrorReturn { Result.withError(Error(it)) }
        }
    }

    private fun mapForecastWeatherResponse():
            SingleTransformer<ForecastWeatherData, Result<ForecastWeather>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn { it.toResultError() }
            //.onErrorReturn { Result.withError(Error(it)) }
        }
    }

    private fun mapAirPollutionResponse():SingleTransformer<AirPollutionData, Result<AirPollution>>{
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
    //private fun getLatitudeParam(): Double = storageRepository.getLat()
    //private fun getLongitudeParam(): Double = storageRepository.getLon()

}

private fun Units.toQueryParam(): String {
    return when (this) {
        Units.Metric -> "metric"
        Units.NotMetric -> "standard"
    }
}