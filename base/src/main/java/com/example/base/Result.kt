package com.example.base
sealed class Result<T> {
    data class OnSuccess<T>(val data: T) : Result<T>()
    data class OnError<T>(val error: Error) : Result<T>()

    companion object {
        @JvmStatic
        fun <T> withValue(data: T): Result<T> = OnSuccess(data)

        @JvmStatic
        fun <T> withError(error: Error): Result<T> = OnError(error)
    }
}
