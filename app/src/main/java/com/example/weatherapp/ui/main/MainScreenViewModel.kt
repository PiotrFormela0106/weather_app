package com.example.weatherapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.controller.PreferencesController
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.CurrentWeather
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.repo.WeatherRepository
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val preferencesController: PreferencesController
) : ViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    private val iconId = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>("https://openweathermap.org/img/wn/03d@2x.png")
    val status = MutableLiveData<Boolean>()
    val weatherData = MutableLiveData<CurrentWeather>()
    val forecastData = MutableLiveData<ForecastWeather>()
    val progress = MutableLiveData<Boolean>(false)
    val cityName = MutableLiveData<String>()

    fun getCurrentWeatherForCity() {
        progress.postValue(true)
        disposable.add(
            weatherRepository.getCurrentWeatherForCity(preferencesController.getCity())
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS) //make progress bar longer (duration)
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
    fun getCurrentWeatherForLocation() {
        progress.postValue(true)
        disposable.add(
            weatherRepository.getCurrentWeatherForLocation(54.27,18.19)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS) //make progress bar longer (duration)
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
    fun getForecastForCity() {
        disposable.add(
            weatherRepository.getForecastForCity("Munich")
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
    fun getForecastForLocation() {
        disposable.add(
            weatherRepository.getForecastForLocation(54.27,18.19)
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

    private fun handleSuccess(data: CurrentWeather) {
        weatherData.value = data
        cityName.postValue(data.cityName)
        iconId.value = data.weather[0].icon
        imageUrl.value = "https://openweathermap.org/img/wn/${iconId.value}@2x.png"
        status.value = true
        preferencesController.saveCity(data.cityName)
    }

    private fun handleSuccess(data: ForecastWeather) {
        Log.i("forecast","success")
        forecastData.value = data
        status.value = true
        //preferencesController.saveCity(data.city.toString())
    }

    private fun handleError(error: Error?) {
        Log.i("forecast","error")

        status.value = false
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
}