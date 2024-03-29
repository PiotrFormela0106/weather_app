package com.example.weatherapp.ui.main

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.data.mappers.toSymbol
import com.example.weatherapp.domain.Error
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.AirPollution
import com.example.weatherapp.domain.models.AirPollutionItem
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.core.UiEvents
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository,
    val resources: Resources
) : ViewModel(), LifecycleObserver {

    private val disposable = CompositeDisposable()
    private val uiEvents = UiEvents<Event>()
    val events: Observable<Event> = uiEvents.stream()

    val status = MutableLiveData(Status.Loading)
    val photoVisibility = MutableLiveData(false)
    val weatherData = MutableLiveData<CurrentWeather>()
    val forecastData = MutableLiveData<ForecastWeather>()
    val airPollutionData = MutableLiveData<AirPollution>()

    private val sunriseFormat = MutableLiveData<String>()
    private val sunsetFormat = MutableLiveData<String>()
    private val iconId = MutableLiveData<String>()
    val weatherImageUrl = MutableLiveData("https://openweathermap.org/img/wn/03d@2x.png")
    val roundedTemperature = MutableLiveData<String>()
    val cityName = MutableLiveData<String>()
    val locationMethod = MutableLiveData(storageRepository.getLocationMethod())
    val date = MutableLiveData<String>()

    init {
        setLang(Locale.getDefault().country)
    }

    enum class Status { Loading, Success, Error }
    inner class ViewState {
        val data = airPollutionData
        private val pollution: LiveData<AirPollutionItem> = Transformations.map(data) { it.list[0] }
        val aqi: LiveData<String> = Transformations.map(pollution) { "${it.main.aqi}" }
        val co: LiveData<String> = Transformations.map(pollution) { "${it.components.co}" }
        val no: LiveData<String> = Transformations.map(pollution) { "${it.components.no}" }
        val no2: LiveData<String> = Transformations.map(pollution) { "${it.components.no2}" }
        val o3: LiveData<String> = Transformations.map(pollution) { "${it.components.o3}" }
        val so2: LiveData<String> = Transformations.map(pollution) { "${it.components.so2}" }
        val pm2_5: LiveData<String> = Transformations.map(pollution) { "${it.components.pm2_5}" }
        val pm10: LiveData<String> = Transformations.map(pollution) { "${it.components.pm10}" }
        val nh3: LiveData<String> = Transformations.map(pollution) { "${it.components.nh3}" }

        val sunrise: LiveData<String> =
            Transformations.map(sunriseFormat) { it.removeRange(5, 8) }
        val sunset: LiveData<String> =
            Transformations.map(sunsetFormat) { it.removeRange(5, 8) }
        val wind: LiveData<String> =
            Transformations.map(weatherData) { "${it.wind.speed} m/s" }
        val humidity: LiveData<String> =
            Transformations.map(weatherData) { "${it.main.humidity} %" }
        val pressure: LiveData<String> =
            Transformations.map(weatherData) { "${it.main.pressure} hPA" }
    }

    fun fetchData() {
        photoVisibility.value = false
        val sdf =
            SimpleDateFormat("EEEE, d MMMM HH:mm", Locale(storageRepository.getLanguage().toData()))
        val currentDate = sdf.format(Date())
        date.postValue(currentDate.toString())
        status.postValue(Status.Loading)
        disposable += Single.zip(
            weatherRepository.getCurrentWeather(),
            weatherRepository.getForecastWeather(),
            weatherRepository.getAirPollution()
        ) { currentWeatherResult, getForecastWeatherResult, airPollutionResult ->
            Triple(
                first = currentWeatherResult,
                second = getForecastWeatherResult,
                third = airPollutionResult
            )
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { result ->
                status.postValue(Status.Success)
                when (val weather = result.first) {
                    is Result.OnSuccess -> {
                        handleSuccess(weather.data)
                    }
                    is Result.OnError -> {
                        handleError(weather.error)
                    }
                }
                when (val forecast = result.second) {
                    is Result.OnSuccess -> {
                        forecast.data.let { forecastData.value = it }
                    }
                    is Result.OnError -> {
                        handleError(forecast.error)
                    }
                }
                when (val pollution = result.third) {
                    is Result.OnSuccess -> {
                        pollution.data.let { airPollutionData.value = it }
                    }
                    is Result.OnError -> {
                        handleError(pollution.error)
                    }
                }
            }
    }

    private fun handleSuccess(data: CurrentWeather) {
        status.postValue(Status.Success)
        weatherData.value = data
        cityName.postValue(data.cityName)
        if (data.weather.isNotEmpty()) {
            iconId.value = data.weather[0].icon
            weatherImageUrl.value = "https://openweathermap.org/img/wn/${iconId.value}@2x.png"
        }
        storageRepository.saveCity(data.cityName)
        storageRepository.saveCoordinates(data.coordinates.lat, data.coordinates.lon)
        roundedTemperature.postValue(
            data.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()
                .toString() + storageRepository.getUnits().toSymbol()
        )

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm:ss")
        val sunrise = Date(data.sys.sunrise * 1000)
        val sunriseTime = sdf.format(sunrise)
        sunriseFormat.postValue(sunriseTime)

        val sunset = Date(data.sys.sunset * 1000)
        val sunsetTime = sdf.format(sunset)
        sunsetFormat.postValue(sunsetTime)

        if (storageRepository.getLocationMethod() == LocationMethod.City
            && storageRepository.getPhotoId().isNotEmpty()
        ) {
            photoVisibility.value = true
        }
    }

    private fun handleError(error: Error) {
        status.postValue(Status.Error)
        Log.e(this::class.java.simpleName, error.message)
    }

    fun onCitiesClick() {
        storageRepository.saveLocationMethod(LocationMethod.Location)
        Event.OnCitiesClick.let { uiEvents.post(it) }
    }

    fun onSettingsClick() {
        Event.OnSettingsClick.let { uiEvents.post(it) }
    }

    private fun setLang(lang: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(lang)
        resources.updateConfiguration(configuration, metrics)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (storageRepository.getLocationMethod() == LocationMethod.City || storageRepository.getLocationMethod() == LocationMethod.Map)
            fetchData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposable.clear()
    }

    sealed class Event {
        object OnCitiesClick : Event()
        object OnSettingsClick : Event()
    }
}
