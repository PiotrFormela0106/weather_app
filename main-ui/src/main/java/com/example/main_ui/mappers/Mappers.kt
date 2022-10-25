package com.example.main_ui.mappers

import com.example.weather_domain.models.Language
import com.example.weather_domain.models.Units

fun Units.toSymbol(): String {
    return when (this) {
        Units.Metric -> CELSIUS
        Units.NotMetric -> KELVIN
    }
}

fun Language.toStringFormat(): String {
    return when (this) {
        Language.ENG -> ENG
        Language.PL -> PL
        Language.DE -> DE
    }
}

private const val PL = "pl"
private const val ENG = "eng"
private const val DE = "de"
private const val CELSIUS = " \u2103"
private const val KELVIN = " K"
