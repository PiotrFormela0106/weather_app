package com.example.weatherapp.ui.additionalinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentViewPagerBinding
    private val args: ViewPagerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_view_pager, container, false
        )
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

        val adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = index
        return binding.root
    }
}
