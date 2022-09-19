package com.example.weatherapp.ui.additionalinfo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ForecastDayDetailedBinding
import com.example.weatherapp.domain.models.ForecastItem
import com.squareup.picasso.Picasso
import java.math.RoundingMode

class ForecastDetailsAdapter(private val forecast: List<ForecastItem>, val context: Context) :
    RecyclerView.Adapter<DetailsViewHolder>() {
    private lateinit var binding: ForecastDayDetailedBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        binding =
            ForecastDayDetailedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater = LayoutInflater.from(parent.context)
        val forecastBinding: ForecastDayDetailedBinding =
            ForecastDayDetailedBinding.inflate(layoutInflater, parent, false)
        return DetailsViewHolder(forecastBinding, context)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val day: ForecastItem = forecast[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return forecast.size
    }
}

class DetailsViewHolder(private val binding: ForecastDayDetailedBinding, val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(day: ForecastItem) {
        val temperature =
            "${day.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()}\u00B0"
        val humidity = "${day.main.humidity} %"
        val windSpeed = "${day.wind.speed} m/s"
        val pressure = "${day.main.pressure} hPa"

        try {
            var date = day.date.removeRange(0, 11)
            date = date.removeRange(5, 8)
            binding.hour.text = date
        } catch (e: IndexOutOfBoundsException) {
            Log.e("Error", e.toString())
        }
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
