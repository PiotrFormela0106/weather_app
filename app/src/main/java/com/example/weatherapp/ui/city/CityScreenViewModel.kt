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
    val photoId = MutableLiveData<String>()

    init {
        fetchCitiesList()
    }

    private fun checkCityWeatherData(city: String, photoId: String) {
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
                            saveCityLocallyInCitiesList(city = it.data.cityName, photoId)
                            storageRepository.saveCity(it.data.cityName)
                            storageRepository.savePhotoId(photoId)
                            uiEvents.post(Event.OnAddCity)
                        } else {
                            fetchCitiesList()
                            val duplicate = allCities.value.orEmpty().find { it.city == cityName }
                            duplicate?.let { it_ -> deleteCity(it_) }
                            storageRepository.saveCity(it.data.cityName)
                            storageRepository.savePhotoId(photoId)
                            saveCityLocallyInCitiesList(city = it.data.cityName, photoId)
                            uiEvents.post(Event.OnAddCity)
                        }
                    }
                    is Result.OnError -> {
                        handleError(it.error)
                        storageRepository.saveCity(currentCity)
                    }
                }
            }
    }

    private fun saveCityLocallyInCitiesList(city: String, photoId: String) {
        cityRepository.insertCity(city, photoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("inserted", "$city inserted!")
                    fetchCitiesList()
                },
                onError = {Log.e("inserted", it.message.orEmpty())}
            )
    }
    fun deleteCity(city: City) {
        cityRepository.deleteCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("removed", "$city removed!")
                    fetchCitiesList()
                }
            )
    }

    fun fetchCitiesList() {
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
        fetchCitiesList()
        cityRepository.deleteAllCities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("status of data", "Data removed!")
                    fetchCitiesList()
                }
            )
    }

    fun getPhotoId(city: String) {
        fetchCitiesList()
        cityRepository.getPhotoId(city)
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
        photoId.value = data
    }

    private fun handleSuccess(data: List<City>) {
        allCities.postValue(data)
    }

    private fun handleError(error: Error?) {
        if (error is CityError) {
            uiEvents.post(Event.OnCityError(error.message))
        }
    }

    fun addCity(place: Place, photoId: String) {
        storageRepository.saveLocationMethod(LocationMethod.City)
        checkCityWeatherData(city = place.name.orEmpty(), photoId = photoId)
    }

    fun useLocation() {
        storageRepository.saveLocationMethod(LocationMethod.Location)
        Event.OnLocation.let { uiEvents.post(it) }
    }

    fun onBack() {
        Event.OnBack.let { uiEvents.post(it) }
    }

    fun useMap() {
        uiEvents.post(Event.OnMaps)
    }

    sealed class Event {
        object OnMaps : Event()
        object OnBack : Event()
        object OnAddCity : Event()
        object OnLocation : Event()
        class OnCityError(val message: String) : Event()
    }
}
