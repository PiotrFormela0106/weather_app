package com.example.weatherapp.ui.main

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.data.repo.WeatherRepositoryImpl.LocationMethod
import com.example.weatherapp.domain.CityError
import com.example.weatherapp.domain.Error
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.UiEvents
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    private val iconId = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>("https://openweathermap.org/img/wn/03d@2x.png")
    val status = MutableLiveData<Status>(Status.Loading)
    val weatherData = MutableLiveData<CurrentWeather>()
    val forecastData = MutableLiveData<ForecastWeather>()
    val airPollutionData = MutableLiveData<AirPollution>()
    val cityName = MutableLiveData<String>()
    val temperature: LiveData<String> =
        Transformations.map(weatherData) { weather -> "${weather.main.temp} C" }
    val aqi: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].main.aqi}" }
    val co: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.co}" }
    val no: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.no}" }
    val no2: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.no2}" }
    val o3: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.o3}" }
    val so2: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.so2}" }
    val pm2_5: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.pm2_5}" }
    val pm10: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.pm10}" }
    val nh3: LiveData<String> =
        Transformations.map(airPollutionData) { airPollution -> "${airPollution.list[0].components.nh3}" }
    private val uiEvents = UiEvents<Event>()
    val events: Observable<Event> = uiEvents.stream()
    val progress = MutableLiveData<Boolean>()
    val lat = MutableLiveData<Double>(84.27)
    val lon = MutableLiveData<Double>(28.19)

    enum class Status { Loading, Success, Error }

    init {
        Log.i("viewModel/units", storageRepository.getUnits())
        storageRepository.saveLocationMethod(LocationMethod.City)
        getCurrentWeather()
        getForecastWeather()
        getAirPollution()
    }


    private fun getCurrentWeather() {
        progress.postValue(true)
        status.postValue(Status.Loading)
        disposable.add(
            weatherRepository.getCurrentWeather(
                city = "Somonino",
                units = storageRepository.getUnits() ?: "metric"
            )
                .subscribeOn(Schedulers.io())
                //.delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        progress.postValue(false)
                        when (it) {
                            is Result.OnSuccess -> handleSuccess(it.data)
                            is Result.OnError -> {
                                handleError(it.error)
                            }
                        }
                    })
        )
    }

    private fun getForecastWeather() {
        disposable.add(
            weatherRepository.getForecastWeather(
                city = "Somonino",
                units = storageRepository.getUnits() ?: "metric"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        when (it) {
                            is Result.OnSuccess -> handleSuccess(it.data)
                            is Result.OnError -> {
                                handleError(it.error)
                            }
                        }
                    })
        )
    }
    /*private fun getCurrentWeather() {
        progress.postValue(true)
        status.postValue(Status.Loading)
        weatherRepository.getCurrentWeather(
            city = "Somonino",
            units = storageRepository.getUnits() ?: "metric"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getForecastWeather() {
            weatherRepository.getForecastWeather(
                city = "Somonino",
                units = storageRepository.getUnits() ?: "metric"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }*/

    private fun getAirPollution() {
        lat.postValue(weatherData.value?.coordinates?.lat)
        lon.postValue(weatherData.value?.coordinates?.lat)
        Log.i("weatherDAta", weatherData.value?.coordinates?.lat.toString())
        Log.i("lonAir", lon.value.toString())
        disposable.add(
            weatherRepository.getAirPollution(lat = 54.27,lon = 18.99)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        when (it) {
                            is Result.OnSuccess -> handleSuccess(it.data)
                            is Result.OnError -> {
                                handleError(it.error)
                            }
                        }
                    }
                )
        )
    }

    private fun handleSuccess(data: CurrentWeather) {
        status.postValue(Status.Success)
        weatherData.value = data
        cityName.postValue(data.cityName)
        iconId.value = data.weather[0].icon
        imageUrl.value = "https://openweathermap.org/img/wn/${iconId.value}@2x.png"
        storageRepository.saveCity(data.cityName)
    }

    private fun handleSuccess(data: ForecastWeather) {
        forecastData.value = data
    }

    private fun handleSuccess(data: AirPollution) {
        airPollutionData.value = data
    }

    private fun handleError(error: Error?) {
        status.postValue(Status.Error)
        if (error is CityError) {
            uiEvents.post(Event.OnCityError(error.message))
        }
    }

    fun onCitiesClick() {
        Event.OnCitiesClick.let { uiEvents.post(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreatePerformTask() {
        Log.i("lifecycleobserver", "I\'m inside Observer of ViewModel ON_CREATE")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyPerformTask() {
        Log.i("lifecycleobserver", "I\'m inside Observer of ViewModel ON_DESTROY")
        disposable.clear()
    }

    sealed class Event {
        object OnCitiesClick : Event()
        class OnCityError(val message: String) : Event()
    }

}