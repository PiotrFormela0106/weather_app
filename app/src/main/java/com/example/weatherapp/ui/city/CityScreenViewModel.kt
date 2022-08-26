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
import com.example.weatherapp.ui.UiEvents
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

    fun checkCity(city: City) {//make it            private fun checkCity(cityName: String)
        storageRepository.saveLocationMethod(LocationMethod.City)//move this line right in OnAddCityClick
        weatherRepository.getCurrentWeather(city = city.city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                when (it) {
                    is Result.OnSuccess -> {
                        val cityName = it.data.cityName
                        if(!allCities.value!!.any { it.city == cityName})
                            insertCity(City(city = it.data.cityName))
                        else
                            uiEvents.post(Event.OnCityDuplicate)
                    }
                    is Result.OnError -> {
                        handleError(it.error)
                    }
                }
            }
    }

    //it can be private if you call this in init block of viewModel instead of onViewCreated fragment
    fun insertCity(city: City) {//private fun saveCity(cityName: String)
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

    fun updateCity(city: City) {//never used
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

    fun getAllCities() {//rename to updateCities()
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
        getAllCities()
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

    fun deleteCityById(cityId: Int){
        getAllCities()
        cityRepository.deleteCityById(cityId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.i("removed", "$cityId removed!")
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
        Event.OnAddCity.let { uiEvents.post(it) }//no need to post event here
        //just do logic here
        checkCity(City(city=cityName.value.orEmpty()))
    }

    fun useLocation(){
        Event.OnLocation.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnAddCity : Event()
        object OnLocation : Event()
        class OnCityError(val message: String) : Event()
        object OnCityDuplicate : Event()
    }
}