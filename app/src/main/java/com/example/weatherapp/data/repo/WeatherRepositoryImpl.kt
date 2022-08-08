package com.example.weatherapp.data.repo

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.mappers.CurrentWeatherData
import com.example.weatherapp.data.mappers.CurrentWeatherDomain
import com.example.weatherapp.data.mappers.toDomain
import com.example.weatherapp.domain.models.*
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.repo.WeatherRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import com.example.weatherapp.domain.Result

class WeatherRepositoryImpl(retrofitClient: RetrofitClient) : WeatherRepository {
    private val api = retrofitClient.api

    override fun getCurrentWeatherForCity(city: String): Single<Result<CurrentWeather>> {
        return api.getData(city,"7a6886b06890c79387cbdf1ebc857ef2","eng","metric")
            .compose(mapCurrentWeatherResponse())
    }

    /*override fun getCurrentWeatherForLocation(
        lat: Long,
        lon: Long
    ): Single<Result<CurrentWeather>> {

    }

    override fun getForecastForCity(city: String): Single<Result<ForecastWeather>> {
        TODO("Not yet implemented")
    }

    override fun getForecastForLocation(lat: Long, lon: Long): Single<Result<ForecastWeather>> {
        TODO("Not yet implemented")
    }*/

    private fun mapCurrentWeatherResponse():
            SingleTransformer<CurrentWeatherData, Result<CurrentWeatherDomain>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it.toDomain()) }
                .onErrorReturn{Result.withError(Error(it))}
        }
    }
}