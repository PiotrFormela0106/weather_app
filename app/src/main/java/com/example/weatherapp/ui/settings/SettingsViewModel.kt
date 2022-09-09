package com.example.weatherapp.ui.settings

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.weatherapp.domain.models.Language
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    val storageRepository: StorageRepository,
    val resources: Resources
) : ViewModel() {
    private val selectionLanguage = MutableLiveData(storageRepository.getLanguage())
    private val selectionUnits = MutableLiveData(storageRepository.getUnits())

    val metric = Transformations.map(selectionUnits) { it == Units.Metric }
    val notMetric = Transformations.map(selectionUnits) { it == Units.NotMetric }

    val polish = Transformations.map(selectionLanguage) { it == Language.PL }
    val english = Transformations.map(selectionLanguage) { it == Language.ENG }

    fun switchUnitsClick() {
        val initialValue = selectionUnits.value ?: Units.Metric
        val finalValue = initialValue.switchUnits()
        storageRepository.saveUnits(finalValue)
        selectionUnits.postValue(finalValue)
    }

    fun switchLanguageClick() {
        val initialValue = selectionLanguage.value ?: Language.ENG
        val finalValue = initialValue.switchLanguage()
        storageRepository.saveLanguage(finalValue)
        selectionLanguage.postValue(finalValue)
    }
}

private fun Units.switchUnits(): Units {
    return when (this) {
        Units.Metric -> Units.NotMetric
        Units.NotMetric -> Units.Metric
    }
}
private fun Language.switchLanguage(): Language {//change handling language for 3 languages
    return when (this) {
        Language.ENG -> Language.PL
        Language.PL -> Language.ENG
    }
}
