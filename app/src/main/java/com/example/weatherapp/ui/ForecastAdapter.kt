package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Cache
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ForecastDayBinding
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.ListElement

class ForecastAdapter(private val forecast: ForecastWeather) :
    RecyclerView.Adapter<MyViewHolder>() {
    private lateinit var binding: ForecastDayBinding
    //private val list: MutableList<ListElement> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        binding = ForecastDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater = LayoutInflater.from(parent.context)
        val forecastBinding: ForecastDayBinding =
            ForecastDayBinding.inflate(layoutInflater, parent, false)
        //val forecastDay = layoutInflater.inflate(R.layout.forecast_day, parent, false)
        return MyViewHolder(forecastBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        lateinit var day: ListElement
        when (position) {
            0 -> day = forecast.list[position]
            1 -> day = forecast.list[8]
            2 -> day = forecast.list[16]
            3 -> day = forecast.list[24]
            4 -> day = forecast.list[32]
        }
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return 5
    }
}

class MyViewHolder(private val binding: ForecastDayBinding) :

    RecyclerView.ViewHolder(binding.root) {
    fun bind(day: ListElement) {
        binding.day.text = day.dtTxt
        binding.temp.text = day.main.temp.toString()
        binding.wind.text = day.wind.speed.toString()
    }
}