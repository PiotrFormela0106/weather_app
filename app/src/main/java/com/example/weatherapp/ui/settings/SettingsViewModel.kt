package com.example.weatherapp.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.R
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.ui.UiEvents
import com.example.weatherapp.ui.main.MainScreenViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    val storageRepository: StorageRepository
) : ViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events: Observable<SettingsViewModel.Event> = uiEvents.stream()
    private val units = storageRepository.getUnits()
    val radioChecked = MutableLiveData<Int>()

    init {
        radioChecked.postValue(
            if (units.equals("metric")) R.id.metric else R.id.non_metric
        )
    }

    fun onRadioButtonClick() {
        Event.OnRadioButtonClick.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnRadioButtonClick : Event()
    }
}