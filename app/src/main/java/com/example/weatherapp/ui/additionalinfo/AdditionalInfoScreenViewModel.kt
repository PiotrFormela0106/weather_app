package com.example.weatherapp.ui.additionalinfo

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.core.UiEvents
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AdditionalInfoScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    val storageRepository: StorageRepository
) : ViewModel(), LifecycleObserver {
    val forecast = MutableLiveData<ForecastWeather?>()
    var dayValue = MutableLiveData<String>()
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    init {
        fetchData()
    }

    private fun fetchData() {
        weatherRepository.getForecastWeather()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when (it) {
                        is Result.OnSuccess -> {
                            forecast.value = it.data
                        }
                        is Result.OnError -> {
                            Log.i("result", "error")
                        }
                    }
                }
            )
    }

    fun onBack() {
        Event.OnBack.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnBack : Event()
    }
}
