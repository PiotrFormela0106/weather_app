package com.example.weatherapp.ui.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.domain.Error
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.core.UiEvents
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {

    private val disposable = CompositeDisposable()
    private val iconId = MutableLiveData<String>()
    val imageUrl = MutableLiveData("https://openweathermap.org/img/wn/03d@2x.png")
    val status = MutableLiveData(Status.Loading)
    val weatherData = MutableLiveData<CurrentWeather>()
    val forecastData = MutableLiveData<ForecastWeather>()
    private val airPollutionData = MutableLiveData<AirPollution>()
    val cityName = MutableLiveData<String>()
    private val sunriseFormat = MutableLiveData<String>()
    private val sunsetFormat = MutableLiveData<String>()
    val sunrise: LiveData<String> =
        Transformations.map(sunriseFormat) { sunrise -> "Sunrise: $sunrise" }
    val sunset: LiveData<String> =
        Transformations.map(sunsetFormat) { sunset -> "Sunset: $sunset" }
    val wind: LiveData<String> =
        Transformations.map(weatherData) { weather -> "Wind speed: ${weather.wind.speed} m/s" }
    val humidity: LiveData<String> =
        Transformations.map(weatherData) { weather -> "Humidity: ${weather.main.humidity} %" }
    val pressure: LiveData<String> =
        Transformations.map(weatherData) { weather -> "Pressure: ${weather.main.pressure} hPa" }
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
    enum class Status { Loading, Success, Error }

    init {
        getCurrentWeather()
        getForecastWeather()
        getAirPollution()
    }

    fun getCurrentWeather() {
        progress.postValue(true)
        status.postValue(Status.Loading)
        disposable.add(
            weatherRepository.getCurrentWeather(
                city = storageRepository.getCity()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        progress.postValue(false)
                        when (it) {
                            is Result.OnSuccess -> {
                                handleSuccess(it.data)
                            }
                            is Result.OnError -> {
                                handleError(it.error)
                            }
                        }
                    }
                )
        )
    }

    fun getForecastWeather() {
        disposable.add(
            weatherRepository.getForecastWeather()
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

    private fun getAirPollution() {
        disposable.add(
            weatherRepository.getAirPollution(lat = 51.19, lon = 18.19)
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

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm:ss")
        val sunrise = Date(data.sys.sunrise * 1000)
        val sunriseTime = sdf.format(sunrise)
        sunriseFormat.postValue(sunriseTime)

        val sunset = Date(data.sys.sunset * 1000)
        val sunsetTime = sdf.format(sunset)
        sunsetFormat.postValue(sunsetTime)
    }

    private fun handleSuccess(data: ForecastWeather) {
        forecastData.value = data
    }

    private fun handleSuccess(data: AirPollution) {
        airPollutionData.value = data
    }

    private fun handleError(error: Error?) {
        status.postValue(Status.Error)
    }

    fun onCitiesClick() {
        storageRepository.saveLocationMethod(LocationMethod.Location)
        Event.OnCitiesClick.let { uiEvents.post(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyPerformTask() {
        Log.i("lifecycleobserver", "I\'m inside Observer of ViewModel ON_DESTROY")
        disposable.clear()
    }

    sealed class Event {
        object OnCitiesClick : Event()
    }
}
