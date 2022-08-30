package com.example.weatherapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ForecastDayBinding
import com.example.weatherapp.domain.models.ForecastItem
import com.example.weatherapp.domain.models.ForecastWeather
import com.squareup.picasso.Picasso
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
        lateinit var day: ForecastItem
        when (position) {
            0 -> day = forecast.list[position]
            1 -> day = forecast.list[8]
            2 -> day = forecast.list[16]
            3 -> day = forecast.list[24]
            4 -> day = forecast.list[32]
            5 -> day = forecast.list[39]
        }
        day.date = day.date.removeRange(10,19)
        day.date = day.date.removeRange(0,5)
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return 6
    }
}

class MyViewHolder(private val binding: ForecastDayBinding) :

    RecyclerView.ViewHolder(binding.root) {
    fun bind(day: ForecastItem) {
        binding.forecast = day
        binding.executePendingBindings()
        Picasso.get()
            .load("https://openweathermap.org/img/wn/${day.weather.get(0).icon}@2x.png")
            .into(binding.imageView2)
    }
}