package com.example.weatherapp.data.repo

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.mappers.*
import com.example.weatherapp.domain.models.*
import com.example.weatherapp.domain.repo.WeatherRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import com.example.weatherapp.domain.Result
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(retrofitClient: RetrofitClient) : WeatherRepository {
    private val api = retrofitClient.api

    override fun getCurrentWeatherForCity(city: String): Single<Result<CurrentWeather>> {
        return api.getCurrentWeatherForCity(
            cityName = city,
            apikey = "7a6886b06890c79387cbdf1ebc857ef2",
            lang = "pl",
            units = "metric"
        )//use named params when you have a lot of parameters of function
            .compose(mapCurrentWeatherResponse())
    }

    override fun getCurrentWeatherForLocation(lat: Double, lon: Double): Single<Result<CurrentWeather>> {
        return api.getCurrentWeatherForLocation(
            lat = lat,
            lon = lon,
            apikey = "7a6886b06890c79387cbdf1ebc857ef2"
        )
            .compose(mapCurrentWeatherResponse())
    }

    override fun getForecastForCity(cityName: String): Single<Result<ForecastWeather>> {
        return api.getForecastForCity(
            cityName = cityName,
            apikey = "7a6886b06890c79387cbdf1ebc857ef2",
            lang = "eng",
            units = "metric"
        )
            .compose(mapForecastWeatherResponse())
    }

    override fun getForecastForLocation(lat: Double, lon: Double): Single<Result<ForecastWeather>> {
        return api.getForecastForLocation(
            lat = lat,
            lon = lon,
            apikey = "7a6886b06890c79387cbdf1ebc857ef2"
        )
            .compose(mapForecastWeatherResponse())
    }

    private fun mapCurrentWeatherResponse():
            SingleTransformer<CurrentWeatherData, Result<CurrentWeatherDomain>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn{Result.withError(Error(it))}
        }
    }
    private fun mapForecastWeatherResponse():
            SingleTransformer<ForecastWeatherData, Result<ForecastWeatherDomain>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn{Result.withError(Error(it))}
        }
    }
}