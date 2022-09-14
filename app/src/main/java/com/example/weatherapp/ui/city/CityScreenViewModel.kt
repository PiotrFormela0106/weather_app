package com.example.weatherapp.ui.city

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.room.City // don't use data classes in ui layer
import com.example.weatherapp.domain.CityError
import com.example.weatherapp.domain.Error
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.repo.CityRepository
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.core.UiEvents
import com.google.android.libraries.places.api.model.Place
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CityScreenViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val allCities = MutableLiveData<List<City>>()
    val cityName = MutableLiveData<String>()
    val placeId = MutableLiveData<String>()

    private fun checkCity(city: String, placeId: String) {
        val currentCity = storageRepository.getCity()
        storageRepository.saveCity(city)
        weatherRepository.getCurrentWeather()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                when (it) {
                    is Result.OnSuccess -> {
                        val cityName = it.data.cityName
                        if (allCities.value.orEmpty().any { it.city == cityName }.not()) {
                            insertCity(city = it.data.cityName, placeId)
                            storageRepository.saveCity(it.data.cityName)
                            storageRepository.savePlaceId(placeId)
                            Event.OnAddCity.let { uiEvents.post(it) }
                        } else {
                            fetchCities()
                            val duplicate = allCities.value.orEmpty().find { it.city == cityName }
                            deleteCity(duplicate!!)
                            storageRepository.saveCity(it.data.cityName)
                            insertCity(city = it.data.cityName, placeId)
                            Event.OnAddCity.let { uiEvents.post(it) }
                        }
                    }
                    is Result.OnError -> {
                        handleError(it.error)
                        storageRepository.saveCity(currentCity)
                    }
                }
            }
    }

    fun insertCity(city: String, placeId: String) {
        cityRepository.insertCity(city, placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("inserted", "$city inserted!")
                    fetchCities()
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
                    fetchCities()
                }
            )
    }

    fun fetchCities() {
        cityRepository.fetchCities()
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
        fetchCities()
        cityRepository.deleteAllCities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("status of data", "Data removed!")
                    fetchCities()
                }
            )
    }

    fun getPlaceId(city: String) {
        fetchCities()
        cityRepository.getPlaceId(city)
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

    private fun handleSuccess(data: String) {
        placeId.value = data
    }

    private fun handleSuccess(data: List<City>) {
        allCities.postValue(data)
    }

    private fun handleError(error: Error?) {
        if (error is CityError) {
            uiEvents.post(Event.OnCityError(error.message))
        }
    }

    fun addCity(place: Place) {
        storageRepository.saveLocationMethod(LocationMethod.City)
        checkCity(city = place.name.orEmpty(), placeId = place.id.orEmpty())
    }

    fun useLocation() {
        storageRepository.saveLocationMethod(LocationMethod.Location)
        Event.OnLocation.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnAddCity : Event()
        object OnLocation : Event()
        class OnCityError(val message: String) : Event()
    }
}
