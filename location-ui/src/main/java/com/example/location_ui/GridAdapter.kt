package com.example.location_ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.city_domain.models.City
import com.squareup.picasso.Picasso

class GridAdapter(private val listOfCities: List<City>, private val context: Context) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var cityImage: ImageView
    private lateinit var cityName: TextView

    override fun getCount(): Int {
        return listOfCities.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.city, null)
        }

        cityImage = convertView!!.findViewById(R.id.city_picture)
        cityName = convertView.findViewById(R.id.cityName)
        cityName.text = listOfCities[position].city

        Picasso.get()
            .load(listOfCities[position].photoId)
            .into(cityImage)

        return convertView
    }
}
