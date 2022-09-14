package com.example.weatherapp.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ForecastDayBinding
import com.example.weatherapp.domain.models.ForecastItem
import com.example.weatherapp.domain.models.ForecastWeather
import com.squareup.picasso.Picasso
import java.math.RoundingMode

class ForecastAdapter(private val forecast: ForecastWeather) :
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
        try {
            var date = day.date.removeRange(0, 5)
            date = date.removeRange(11, 14)
            binding.day.text = date
        } catch (e: IndexOutOfBoundsException) {
            Log.e("Exception", e.toString())
        }
        binding.forecast = day
        binding.temp.text = day.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt().toString()
        binding.executePendingBindings()
        Picasso.get()
            .load("https://openweathermap.org/img/wn/${day.weather.get(0).icon}@2x.png")
            .into(binding.imageView2)
    }
}
