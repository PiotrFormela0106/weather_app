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
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.toData
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
import io.reactivex.rxjava3.core.Observable.merge
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.merge
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository,
    val resources: Resources
) : ViewModel(), LifecycleObserver {

    private val disposable = CompositeDisposable()
    private val uiEvents = UiEvents<Event>()
    private val sunriseFormat = MutableLiveData<String>()
    private val sunsetFormat = MutableLiveData<String>()
    private val iconId = MutableLiveData<String>()
    val imageUrl = MutableLiveData("https://openweathermap.org/img/wn/03d@2x.png")
    val status = MutableLiveData(Status.Loading)
    val weatherData = MutableLiveData<CurrentWeather>()
    val forecastData = MutableLiveData<ForecastWeather>()
    val cityName = MutableLiveData<String>()
    val events: Observable<Event> = uiEvents.stream()
    val progress = MutableLiveData<Boolean>()
    val airPollutionData = MutableLiveData<AirPollution>()
    val locationMethod = MutableLiveData(storageRepository.getLocationMethod())
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
            Transformations.map(sunriseFormat) { "${resources.getString(R.string.sunrise)}: $it"}
        val sunset: LiveData<String> =
            Transformations.map(sunsetFormat) { "${resources.getString(R.string.sunset)}: $it" }
        val wind: LiveData<String> =
            Transformations.map(weatherData) { "${resources.getString(R.string.wind)}: ${it.wind.speed} m/s" }
        val humidity: LiveData<String> =
            Transformations.map(weatherData) { "${resources.getString(R.string.humidity)}: ${it.main.humidity} %" }
        val pressure: LiveData<String> =
            Transformations.map(weatherData) { "${resources.getString(R.string.pressure)}: ${it.main.pressure} hPA" }
        val temperature: LiveData<String> =
            Transformations.map(weatherData) { "${it.main.temp} C" }
    }

    init {
        setLang(storageRepository.getLanguage().toData())
        getCurrentWeather()
        getForecastWeather()
        getAirPollution()
    }

    fun getCurrentWeather() {
        progress.postValue(true)
        status.postValue(Status.Loading)
        disposable.add(
            weatherRepository.getCurrentWeather()
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

    private fun setLang(lang: String){
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(lang)
        resources.updateConfiguration(configuration,metrics)
        //onConfigurationChanged(configuration)

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
        disposable.clear()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        setLang(storageRepository.getLanguage().toData())
        getCurrentWeather()
        getForecastWeather()
    }

    sealed class Event {
        object OnCitiesClick : Event()
    }
}
