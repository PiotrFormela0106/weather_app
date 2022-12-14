package com.example.weatherapp.ui.main

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.databinding.ForecastDayBinding
import com.example.weatherapp.domain.models.ForecastItem
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.repo.StorageRepository
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ForecastAdapter @Inject constructor(private val forecast: ForecastWeather, val storageRepository: StorageRepository, val resources: Resources) :
    RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var binding: ForecastDayBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        //setLang(storageRepository.getLanguage().toData())
        binding = ForecastDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater = LayoutInflater.from(parent.context)
        val forecastBinding: ForecastDayBinding =
            ForecastDayBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(forecastBinding, storageRepository, resources)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val day = forecast.list[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return forecast.list.size
    }

    /*private fun setLang(lang: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(lang)
        resources.updateConfiguration(configuration, metrics)
    }*/
}

class MyViewHolder (private val binding: ForecastDayBinding, val storageRepository: StorageRepository, val resources: Resources) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(day: ForecastItem) {
        Log.i("locale",storageRepository.getLanguage().toData())
        val sdf = SimpleDateFormat("dd MMM HH:mm", Locale(storageRepository.getLanguage().toData()))
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val dateAndTimeFormat2 = sdf.format(Date(day.dateLong * 1000))
        binding.day.text = dateAndTimeFormat2
        binding.forecast = day
        val temperature = "${day.main.temp.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toInt()}\u00B0"
        binding.temp.text = temperature
        binding.executePendingBindings()
        if (day.weather.isNotEmpty()) {
            Picasso.get()
                .load("https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png")
                .into(binding.imageView2)
        }
    }
}
