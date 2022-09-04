package com.example.weatherapp.ui.additionalinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ForecastDayDetailedBinding
import com.example.weatherapp.domain.models.ForecastItem
import com.squareup.picasso.Picasso

class ForecastDetailsAdapter(private val forecast: List<ForecastItem>) :
    RecyclerView.Adapter<DetailsViewHolder>() {
    private lateinit var binding: ForecastDayDetailedBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        binding =
            ForecastDayDetailedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater = LayoutInflater.from(parent.context)
        val forecastBinding: ForecastDayDetailedBinding =
            ForecastDayDetailedBinding.inflate(layoutInflater, parent, false)
        return DetailsViewHolder(forecastBinding)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val day: ForecastItem = forecast[position]
        day.date = day.date.removeRange(0, 11)
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return forecast.size
    }
}

class DetailsViewHolder(private val binding: ForecastDayDetailedBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(day: ForecastItem) {
        val temperature = "${day.main.temp}"
        val humidity = "Humidity: ${day.main.humidity} %"
        val windSpeed = "Wind speed: ${day.wind.speed} m/s"
        val pressure = "Pressure: ${day.main.pressure} hPa"

        binding.tempDetailed.text = temperature
        binding.humidity.text = humidity
        binding.windSpeed.text = windSpeed
        binding.pressure.text = pressure
        binding.forecast = day
        binding.executePendingBindings()
        Picasso.get()
            .load("https://openweathermap.org/img/wn/${day.weather.get(0).icon}@2x.png")
            .into(binding.weatherIcon)
    }
}
