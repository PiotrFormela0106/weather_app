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
    val photoId = MutableLiveData<String>()

    //rename fun checkCityWeatherData
    private fun checkCity(city: String, photoId: String) {
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
                            insertCity(city = it.data.cityName, photoId)
                            storageRepository.saveCity(it.data.cityName)
                            storageRepository.savePhotoId(photoId)
                            Event.OnAddCity.let { uiEvents.post(it) }//no need in let operator here
                        } else {
                            fetchCities()
                            val duplicate = allCities.value.orEmpty().find { it.city == cityName }
                            deleteCity(duplicate!!)// duplicat can be null and you'll get crash
                            storageRepository.saveCity(it.data.cityName)
                            insertCity(city = it.data.cityName, photoId)
                            Event.OnAddCity.let { uiEvents.post(it) }//no need in let operator here
                        }
                    }
                    is Result.OnError -> {
                        handleError(it.error)
                        storageRepository.saveCity(currentCity)
                    }
                }
            }
    }

    //rename fun saveCityLocallyInCitiesList()
    private fun insertCity(city: String, photoId: String) {
        cityRepository.insertCity(city, photoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("inserted", "$city inserted!")
                    fetchCities()
                }
            )
    }
    private fun deleteCity(city: City) {
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


    // rename fun fetchCitiesList()
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

    fun deleteAllCities() {// it's not used
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

    fun getPhotoId(city: String) {
        fetchCities()
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
        placeId.value = data
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
        checkCity(city = place.name.orEmpty(), photoId = photoId)
    }

    fun useLocation() {
        storageRepository.saveLocationMethod(LocationMethod.Location)
        Event.OnLocation.let { uiEvents.post(it) }
    }

    fun onBack() {
        Event.OnBack.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnBack : Event()
        object OnAddCity : Event()
        object OnLocation : Event()
        class OnCityError(val message: String) : Event()
    }
}
