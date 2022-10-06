package com.example.weatherapp.ui.additionalinfo

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.Result
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.repo.WeatherRepository
import com.example.weatherapp.ui.core.UiEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdditionalInfoScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel(), LifecycleObserver {
    val forecast = MutableLiveData<ForecastWeather?>()
    var dayValue = MutableLiveData<String>()
    private val uiEvents = UiEvents<Event>()
    val events: Flow<Event> = uiEvents.events()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = weatherRepository.getForecastWeather()
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.OnSuccess -> result.data.let { forecast.value = it }
                        is Result.OnError -> Log.i("result", "error")
                    }
                }
            }
        }
    }

    fun onBack() {
        Event.OnBack.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnBack : Event()
    }
}
