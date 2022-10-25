package com.example.settings_ui

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.base.UiEvents
import com.example.storage_domain.repo.StorageRepository
import com.example.weather_domain.models.Language
import com.example.weather_domain.models.Units
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val storageRepository: StorageRepository,
    val resources: Resources
) : ViewModel() {
    private val selectionLanguage = MutableLiveData(storageRepository.getLanguage())
    val selectionUnits = MutableLiveData(storageRepository.getUnits())
    val language = MutableLiveData(storageRepository.getLanguage().toId())
    val metric = Transformations.map(selectionUnits) { it == Units.Metric }
    val notMetric = Transformations.map(selectionUnits) { it == Units.NotMetric }
    private val uiEvents = UiEvents<Event>()
    val events: Flow<Event> = uiEvents.events()

    fun switchUnitsClick() {
        val initialValue = selectionUnits.value ?: Units.Metric
        val finalValue = initialValue.switchUnits()
        storageRepository.saveUnits(finalValue)
        selectionUnits.postValue(finalValue)
    }

    fun switchLanguageClick() {
        var value = selectionLanguage.value ?: Language.ENG
        when (language.value) {
            R.id.eng -> value = Language.ENG
            R.id.pl -> value = Language.PL
            R.id.de -> value = Language.DE
        }
        storageRepository.saveLanguage(value)
        selectionLanguage.postValue(value)
    }

    private fun Language.toId(): Int {
        return when (this) {
            Language.ENG -> R.id.eng
            Language.PL -> R.id.pl
            Language.DE -> R.id.de
        }
    }

    private fun Units.switchUnits(): Units {
        return when (this) {
            Units.Metric -> Units.NotMetric
            Units.NotMetric -> Units.Metric
        }
    }

    fun onCancelClick() {
        Event.OnCancelClick.let { uiEvents.post(it) }
    }

    sealed class Event {
        object OnCancelClick : Event()
    }
}
