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

private const val LANG_PL = "pl"
private const val LANG_ENG = "eng"
private const val METRIC = "METRIC"

class WeatherRepositoryImpl @Inject constructor(retrofitClient: RetrofitClient) :
    WeatherRepository {
    private val api = retrofitClient.api
    override fun getCurrentWeatherForCity(city: String): Single<Result<CurrentWeather>> {
        return api.getCurrentWeatherForCity(
            cityName = city,
            apikey = API_KEY,
            lang = LANG_PL,
            units = METRIC
        )
            .compose(mapCurrentWeatherResponse())
    }

    override fun getCurrentWeatherForLocation(
        lat: Double,
        lon: Double
    ): Single<Result<CurrentWeather>> {
        return api.getCurrentWeatherForLocation(lat = lat, lon = lon, apikey = API_KEY)
            .compose(mapCurrentWeatherResponse())
    }

    override fun getForecastForCity(cityName: String): Single<Result<ForecastWeather>> {
        return api.getForecastForCity(
            cityName = cityName,
            apikey = API_KEY,
            lang = LANG_PL,
            units = METRIC
        )
            .compose(mapForecastWeatherResponse())
    }

    override fun getForecastForLocation(lat: Double, lon: Double): Single<Result<ForecastWeather>> {
        return api.getForecastForLocation(lat = lat, lon = lon, apikey = API_KEY)
            .compose(mapForecastWeatherResponse())
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
    private fun <T> Throwable.toResultError(): Result<T> {
        val error = this.toError()
        return Result.withError<T>(error)
    }

}