package com.example.weatherapp.ui.settings

import androidx.lifecycle.*
import com.example.weatherapp.R
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import com.example.weatherapp.ui.UiEvents
import com.example.weatherapp.ui.main.MainScreenViewModel
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    val storageRepository: StorageRepository
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