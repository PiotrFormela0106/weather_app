package com.example.info_ui

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_domain.models.ForecastWeather
import com.example.weather_domain.repo.WeatherRepository
import com.example.base.ui.core.UiEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.weather_domain.Result

class AdditionalInfoScreenViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel(), LifecycleObserver {
    val forecast = MutableLiveData<ForecastWeather?>()
    var dayValue = MutableLiveData<String>()
    private val uiEvents = com.example.base.ui.core.UiEvents<Event>()
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
