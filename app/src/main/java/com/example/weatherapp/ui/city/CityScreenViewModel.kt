package com.example.weatherapp.ui.city

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.repo.CityRepositoryImpl
import com.example.weatherapp.data.room.City
import com.example.weatherapp.domain.CityError
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.repo.CityRepository
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.UiEvents
import com.example.weatherapp.ui.main.MainScreenViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import com.example.weatherapp.domain.Error


class CityScreenViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {
    private val uiEvents = UiEvents<CityScreenViewModel.Event>()
    val events: Observable<CityScreenViewModel.Event> = uiEvents.stream()
    val allCities = MutableLiveData<List<City>>()
    val cityName = MutableLiveData<String>()

    fun checkCity(city: City) {
        weatherRepository.getCurrentWeather(city = city.city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                when (it) {
                    is Result.OnSuccess -> {
                        val cityName = it.data.cityName
                        if(!allCities.value!!.any { it.city == cityName})
                            insertCity(City(it.data.cityName))
                        else
                            Log.i("City error","There is already that city in your list!")
                    }
                    is Result.OnError -> {
                        handleError(it.error)
                    }
                }
            }
    }

    fun insertCity(city: City) {
        cityRepository.insertCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("inserted", "$city inserted!")
                    getAllCities()
                }
            )

    }

    fun updateCity(city: City) {
        cityRepository.updateCity(city)

    }

    fun deleteCity(city: City) {
        cityRepository.deleteCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("removed", "$city removed!")
                    getAllCities()
                }
            )
    }

    fun getAllCities() {
        cityRepository.getAllCities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when (it) {
                        is Result.OnSuccess -> {
                            handleSuccess(it.data)
                        }
                        is Result.OnError -> {}
                    }
                }
            )
    }

    fun deleteAllCities() {
        cityRepository.deleteAllCities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("status of data", "Data removed!")
                    getAllCities()
                }
            )
    }

    private fun handleSuccess(data: List<City>) {
        allCities.postValue(data)
    }

    private fun handleError(error: Error?) {
        if (error is CityError) {
            uiEvents.post(Event.OnCityError(error.message))
        }
    }

    fun addCity() {
        CityScreenViewModel.Event.OnAddCity.let { uiEvents.post(it) }
    }

    fun removeCities() {
        CityScreenViewModel.Event.OnRemoveCities.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnAddCity : Event()
        object OnRemoveCities : Event()
        class OnCityError(val message: String) : Event()
    }
}