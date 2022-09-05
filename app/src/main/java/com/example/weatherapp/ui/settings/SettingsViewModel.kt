package com.example.weatherapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.weatherapp.domain.models.Units
import com.example.weatherapp.domain.repo.StorageRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {
    private val selection = MutableLiveData(storageRepository.getUnits())
    val metric: LiveData<Boolean> = Transformations.map(selection) { it == Units.Metric }
    val notMetric: LiveData<Boolean> = Transformations.map(selection) { it == Units.NotMetric }

    fun switchUnitsClick() {
        val initialValue = selection.value ?: Units.Metric
        val finalValue = initialValue.switchUnits()
        storageRepository.saveUnits(finalValue)
        selection.postValue(finalValue)
    }
}

private fun Units.switchUnits(): Units {
    return when (this) {
        Units.Metric -> Units.NotMetric
        Units.NotMetric -> Units.Metric
    }
}
