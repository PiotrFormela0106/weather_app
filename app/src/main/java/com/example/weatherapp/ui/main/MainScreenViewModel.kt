package com.example.weatherapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.domain.models.CurrentWeather
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.repo.WeatherRepository
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel(), LifecycleObserver {
    private val disposable = CompositeDisposable()
    private val iconId = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>("https://openweathermap.org/img/wn/03d@2x.png")
    val status = MutableLiveData<Boolean>()
    val weatherData = MutableLiveData<CurrentWeather>()
    val progress = MutableLiveData<Boolean>(false)
    val cityName = MutableLiveData<String>()
    val cityArg = MutableLiveData<String>()

    fun getCurrentWeatherForCity() {
        progress.postValue(true)
        disposable.add(
            weatherRepository.getCurrentWeatherForCity("Somonino")
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

    private fun handleSuccess(data: CurrentWeather) {
        weatherData.value = data
        cityName.postValue(data.cityName)
        iconId.value = data.weather[0].icon
        imageUrl.value = "https://openweathermap.org/img/wn/${iconId.value}@2x.png"
        status.value = true
        Log.i("Czy dziala", data.weather[0].description)

    }

    private fun handleError(error: Error?) {
        Log.i("Czy dziala","nie")
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