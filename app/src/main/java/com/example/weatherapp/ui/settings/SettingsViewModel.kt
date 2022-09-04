package com.example.weatherapp.ui.settings

import androidx.lifecycle.*
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.ui.core.UiEvents
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events: Observable<Event> = uiEvents.stream()

    private val selection = MutableLiveData(storageRepository.getUnits())

    val metric: LiveData<Boolean> = Transformations.map(selection) { it == Units.Metric }
    val notMetric: LiveData<Boolean> = Transformations.map(selection) { it == Units.NotMetric }

    fun switchUnitsClick() {
        val initialValue = selection.value ?: Units.Metric
        val finalValue = initialValue.switchUnits()
        storageRepository.saveUnits(finalValue)
        selection.postValue(finalValue)
    }

    sealed class Event {
    }
}

private fun Units.switchUnits(): Units {
    return when (this) {
        Units.Metric -> Units.NotMetric
        Units.NotMetric -> Units.Metric
    }
}