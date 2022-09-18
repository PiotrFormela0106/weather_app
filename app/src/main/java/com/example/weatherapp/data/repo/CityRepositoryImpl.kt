package com.example.weatherapp.data.repo

import android.content.Context
import com.example.weatherapp.data.room.City
import com.example.weatherapp.data.room.CityDao
import com.example.weatherapp.data.room.CityDatabase
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.repo.CityRepository
import com.example.weatherapp.domain.toError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(context: Context) : CityRepository {
    private var cityDao: CityDao

    init {
        val database = CityDatabase
            .getInstance(context.applicationContext)
        cityDao = database.cityDao()
    }

    override fun insertCity(cityName: String, photoId: String): Completable {
        return cityDao.insert(City(city = cityName, photoId = photoId))
    }

    override fun deleteCity(city: City): Completable {
        return cityDao.delete(city)
    }

    override fun fetchCities(): Single<Result<List<City>>> {
        return cityDao.getAllCities()
            .compose(mapCities())
    }

    override fun deleteAllCities(): Completable {
        return cityDao.deleteAllCities()
    }

    override fun getPhotoId(city: String): Single<Result<String>> {
        return cityDao.getPhotoId(city)
            .compose(mapPhotoId())
    }

    private fun mapCities():
        SingleTransformer<List<City>, Result<List<City>>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it) }
                .onErrorReturn { it.toResultError() }
        }
    }

    private fun mapPhotoId():
        SingleTransformer<String, Result<String>> {
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it) }
                .onErrorReturn { it.toResultError() }
        }
    }

    private fun <T> Throwable.toResultError(): Result<T> {
        val error = this.toError()
        return Result.withError<T>(error)
    }
}
