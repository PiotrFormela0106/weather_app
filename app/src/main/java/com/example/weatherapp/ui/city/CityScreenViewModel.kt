package com.example.weatherapp.ui.city

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.room.City//don't use data classes in ui layer
import com.example.weatherapp.domain.CityError
import com.example.weatherapp.domain.Error
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.repo.CityRepository
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.core.UiEvents
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CityScreenViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {
    private val uiEvents = UiEvents<Event>()
    val events: Observable<Event> = uiEvents.stream()
    val allCities = MutableLiveData<List<City>>()
    val cityName = MutableLiveData<String>()

    private fun checkCity(city: String) {
        weatherRepository.getCurrentWeather(city = city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                when (it) {
                    is Result.OnSuccess -> {
                        val cityName1 = it.data.cityName
                        if(!allCities.value!!.any { it.city == cityName1}) {
                            insertCity(city = it.data.cityName)
                            storageRepository.saveCity(it.data.cityName)
                            Event.OnAddCity.let { uiEvents.post(it) }
                        }
                        else {
                            uiEvents.post(Event.OnCityDuplicate)
                        }
                    }
                    is Result.OnError -> {
                        handleError(it.error)
                    }
                }
            }
    }

    private fun insertCity(city: String) {
        cityRepository.insertCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("inserted", "$city inserted!")
                    updateCities()
                }
            )
    }
    fun deleteCity(city: City) {
        cityRepository.deleteCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("removed", "$city removed!")
                    updateCities()
                }
            )
    }

    fun updateCities() {
        cityRepository.updateCities()
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
        updateCities()
        cityRepository.deleteAllCities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("status of data", "Data removed!")
                    updateCities()
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
        storageRepository.saveLocationMethod(LocationMethod.City)
        checkCity(city=cityName.value.orEmpty())
    }

    fun useLocation(){
        storageRepository.saveLocationMethod(LocationMethod.Location)
        Event.OnLocation.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnAddCity : Event()
        object OnLocation : Event()
        class OnCityError(val message: String) : Event()
        object OnCityDuplicate : Event()
    }
}