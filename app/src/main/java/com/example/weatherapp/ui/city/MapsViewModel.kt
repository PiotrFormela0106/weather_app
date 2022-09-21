package com.example.weatherapp.ui.city

import androidx.lifecycle.ViewModel
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.ui.core.UiEvents
import javax.inject.Inject

class MapsViewModel @Inject constructor(val storageRepository: StorageRepository) : ViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    fun setLangLong(lang: Double, long: Double) {
        storageRepository.saveLocationMethod(LocationMethod.Map)
        storageRepository.saveCoordinates(lang, long)
    }

    fun onPickPlace() {
        uiEvents.post(Event.OnPickPlace)
    }

    fun onBack() {
        uiEvents.post(Event.OnBack)
    }

    sealed class Event {
        object OnBack : Event()
        object OnPickPlace : Event()
    }
}
