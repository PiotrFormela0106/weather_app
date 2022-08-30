package com.example.weatherapp.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.room.City
import com.example.weatherapp.databinding.CityBinding

class CityAdapter(private val listOfCites: List<City>): RecyclerView.Adapter<CityViewHolder>(){
    private lateinit var binding: CityBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityViewHolder {
        binding = CityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater = LayoutInflater.from(parent.context)
        val cityBinding: CityBinding =
            CityBinding.inflate(layoutInflater, parent, false)

        return CityViewHolder(cityBinding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city: City = listOfCites[position]
        holder.bind(city)
    }

    override fun getItemCount(): Int {
        return listOfCites.size
    }

}
class CityViewHolder(private val binding: CityBinding) :

    RecyclerView.ViewHolder(binding.root) {
    fun bind(city: City) {
        binding.city = city
        binding.executePendingBindings()
    }
}