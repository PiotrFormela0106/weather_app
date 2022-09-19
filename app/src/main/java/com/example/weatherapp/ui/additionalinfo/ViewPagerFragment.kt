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

//Please rename to ForecastDetailsFragment and matching viewModel and args
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
        val day = args.day.removeRange(2, 12)// what are these magical numbers? IndexOutOfBoundException
        val item = days.find { it.contains(day) }//can be null
        val index = days.indexOf(item)

        val fragmentList = arrayListOf(
            AdditionalInfoScreenFragment(days[0]),//  IndexOutOfBoundException
            AdditionalInfoScreenFragment(days[1]),//  IndexOutOfBoundException
            AdditionalInfoScreenFragment(days[2]),//  IndexOutOfBoundException
            AdditionalInfoScreenFragment(days[3]),//  IndexOutOfBoundException
            AdditionalInfoScreenFragment(days[4]),//  IndexOutOfBoundException
            AdditionalInfoScreenFragment(days[5])//  IndexOutOfBoundException
        )

        val adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = index
        return binding.root
    }
}
