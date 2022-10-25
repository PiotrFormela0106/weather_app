package com.example.location_ui

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Error
import com.example.base.Result
import com.example.base.UiEvents
import com.example.city_domain.models.City
import com.example.city_domain.repo.CityRepository
import com.example.storage_domain.repo.StorageRepository
import com.example.weather_domain.CityError
import com.example.weather_domain.models.LocationMethod
import com.example.weather_domain.repo.WeatherRepository
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CityScreenViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {
    private val uiEvents = UiEvents<Event>()
    val events: Flow<Event> = uiEvents.events()
    val allCities = MutableLiveData<List<City>>()
    val cityName = MutableLiveData<String>()
    val photoId = MutableLiveData<String>()

    init {
        fetchCitiesList()
    }

    private fun checkCityWeatherData(city: String, photoId: String) {
        val currentCity = storageRepository.getCity()
        storageRepository.saveCity(city)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = weatherRepository.getCurrentWeather()
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.OnSuccess -> {
                            val cityName = result.data.cityName
                            if (allCities.value.orEmpty().any { it.city == cityName }.not()) {
                                saveCityLocallyInCitiesList(city = result.data.cityName, photoId)
                                storageRepository.saveCity(result.data.cityName)
                                storageRepository.savePhotoId(photoId)
                                uiEvents.post(Event.OnAddCity)
                            } else {
                                fetchCitiesList()
                                val duplicate =
                                    allCities.value.orEmpty().find { it.city == cityName }
                                duplicate?.let { it_ -> deleteCity(it_) }
                                storageRepository.saveCity(result.data.cityName)
                                storageRepository.savePhotoId(photoId)
                                saveCityLocallyInCitiesList(city = result.data.cityName, photoId)
                                uiEvents.post(Event.OnAddCity)
                            }
                        }
                        is Result.OnError -> {
                            handleError(result.error)
                            storageRepository.saveCity(currentCity)
                        }
                    }
                }
            }
        }
    }

    private fun saveCityLocallyInCitiesList(city: String, photoId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cityRepository.insertCity(city, photoId)
                Log.i("inserted", "$city inserted!")
                fetchCitiesList()
            }
        }
    }

    private fun deleteCity(city: City) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cityRepository.deleteCity(city)
                fetchCitiesList()
            }
        }
    }

    private fun fetchCitiesList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = cityRepository.fetchCities()
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.OnSuccess -> {
                            handleSuccess(result.data)
                        }
                        is Result.OnError -> {}
                    }
                }
            }
        }
    }

    fun deleteAllCities() {
        fetchCitiesList()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cityRepository.deleteAllCities()
                fetchCitiesList()
            }
        }
    }

    fun getPhotoId(city: String) {
        fetchCitiesList()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = cityRepository.getPhotoId(city)
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.OnSuccess -> {
                            handleSuccess(result.data)
                        }
                        is Result.OnError -> {}
                    }
                }
            }
        }
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
            Log.i("error", error.message)
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
