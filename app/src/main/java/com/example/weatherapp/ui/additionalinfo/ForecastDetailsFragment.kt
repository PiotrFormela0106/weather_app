package com.example.weatherapp.ui.additionalinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentForecastDetailsBinding

class ForecastDetailsFragment : Fragment() {
    private lateinit var binding: FragmentForecastDetailsBinding
    private val args: ForecastDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_forecast_details, container, false
        )
        try {
            val days = args.days
            val day = args.day.removeRange(2, 12)
            val item = days.find { it.contains(day) }
            val index = days.indexOf(item)

            val fragmentList = arrayListOf(
                AdditionalInfoScreenFragment(days[0]),
                AdditionalInfoScreenFragment(days[1]),
                AdditionalInfoScreenFragment(days[2]),
                AdditionalInfoScreenFragment(days[3]),
                AdditionalInfoScreenFragment(days[4]),
                AdditionalInfoScreenFragment(days[5])
            )
            val adapter =
                ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
            binding.viewPager.adapter = adapter
            binding.viewPager.currentItem = index
        } catch (e: IndexOutOfBoundsException) {
            Log.e("error", e.toString())
        }
        return binding.root
    }
}
