package com.example.location_ui

import androidx.lifecycle.ViewModel
import com.example.base.UiEvents
import com.example.storage_domain.repo.StorageRepository
import com.example.weather_domain.models.LocationMethod
import kotlinx.coroutines.flow.Flow

class MapsViewModel (val storageRepository: StorageRepository) : ViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events: Flow<Event> = uiEvents.events()

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
