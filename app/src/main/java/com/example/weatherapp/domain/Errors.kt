package com.example.weatherapp.domain

import android.util.Log
import retrofit2.HttpException

// private const val CITY_ERROR = "HTTP 404 Not Found"
private const val CITY_ERROR = "city not found"
private const val CITY_ERROR2 = "Nothing to geocode"

sealed class Error(
    val message: String,
    val throwable: Throwable? = null
)

open class CityError(
    message: String = "There is no such city!",
    throwable: Throwable? = null
) : Error(message, throwable)

open class NetworkError(
    message: String = "Network Error",
    throwable: Throwable? = null
) : Error(message, throwable)

fun Throwable.toError(): com.example.weatherapp.domain.Error {
    return when (this) {
        is HttpException -> {
            val throwable = this
            val message = throwable.response()?.errorBody()?.string().orEmpty()
            Log.i("class error", message)
            when {
                message.contains(CITY_ERROR) || message.contains(CITY_ERROR2) -> CityError(throwable = throwable)
                else -> NetworkError(throwable = this)
            }
        }
        else -> NetworkError(throwable = this)
    }
}
