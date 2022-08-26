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
    init{
        val database = CityDatabase
            .getInstance(context.applicationContext)
        cityDao = database!!.cityDao()
    }

    override fun insertCity(city: City): Completable {
        return cityDao.insert(city)
    }

    override fun updateCity(city: City) {
        cityDao.update(city)
    }

    override fun deleteCity(city: City): Completable {
        return cityDao.delete(city)
    }

    override fun getAllCities(): Single<Result<List<City>>> {
        return cityDao.getAllCities()//revert in back order
            .compose(mapCities())
    }

    override  fun getCurrentCity(): Single<Result<City>>{
        //todo add real implementation
        return Single.just(Result.withValue(City("Krakow")))//it's a STUB
    }

    override fun deleteAllCities(): Completable {
        return cityDao.deleteAllCities()
    }


    private fun mapCities():
            SingleTransformer<List<City>, Result<List<City>>>{
        return SingleTransformer { upstream ->
            upstream
                .map { Result.withValue(it) }
                .onErrorReturn {it.toResultError()}
        }
    }

    private fun <T> Throwable.toResultError(): Result<T> {
        val error = this.toError()
        return Result.withError<T>(error)
    }

}