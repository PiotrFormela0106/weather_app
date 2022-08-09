package com.example.weatherapp.data.repo

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.mappers.CurrentWeatherData
import com.example.weatherapp.data.mappers.CurrentWeatherDomain
import com.example.weatherapp.data.mappers.toDomain
import com.example.weatherapp.domain.models.*
import com.example.weatherapp.domain.repo.WeatherRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import com.example.weatherapp.domain.Result
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(retrofitClient: RetrofitClient) : WeatherRepository {
    private val api = retrofitClient.api

    override fun getCurrentWeatherForCity(city: String): Single<Result<CurrentWeather>> {
        return api.getCurrentWeatherForCity(city,"7a6886b06890c79387cbdf1ebc857ef2","pl","metric")
            .compose(mapCurrentWeatherResponse())
    }

    override fun getCurrentWeatherForLocation(lat: Double, lon: Double): Single<Result<CurrentWeather>> {
        return api.getCurrentWeatherForLocation(lat,lon,"7a6886b06890c79387cbdf1ebc857ef2")
            .compose(mapCurrentWeatherResponse())
    }

    private fun mapCurrentWeatherResponse():
            SingleTransformer<CurrentWeatherData, Result<CurrentWeatherDomain>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn{Result.withError(Error(it))}
        }
    }
}