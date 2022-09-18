package com.example.weatherapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ForecastDayBinding
import com.example.weatherapp.domain.models.ForecastItem
import com.example.weatherapp.domain.models.ForecastWeather
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

class ForecastAdapter @Inject constructor(private val forecast: ForecastWeather) :
    RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var binding: ForecastDayBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        binding = ForecastDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater = LayoutInflater.from(parent.context)
        val forecastBinding: ForecastDayBinding =
            ForecastDayBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(forecastBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val day = forecast.list[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return forecast.list.size
    }
}

class MyViewHolder(private val binding: ForecastDayBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(day: ForecastItem) {
        val sdf = SimpleDateFormat("dd-MMM HH:mm")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val dateAndTimeFormat2 = sdf.format(Date(day.dateLong * 1000))
        binding.day.text = dateAndTimeFormat2
        binding.forecast = day
        val temperature = "${day.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()}\u00B0"
        binding.temp.text = temperature
        binding.executePendingBindings()
        Picasso.get()
            .load("https://openweathermap.org/img/wn/${day.weather.get(0).icon}@2x.png")
            .into(binding.imageView2)
    }
}
