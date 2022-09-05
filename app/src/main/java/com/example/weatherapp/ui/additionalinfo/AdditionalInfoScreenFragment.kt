package com.example.weatherapp.ui.additionalinfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAdditionalInfoScreenBinding
import com.example.weatherapp.di.RepositoryModule
import com.example.weatherapp.domain.models.ForecastItem
import javax.inject.Inject
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment

class AdditionalInfoScreenFragment : DaggerFragment() {
    private lateinit var binding: FragmentAdditionalInfoScreenBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: AdditionalInfoScreenViewModel by viewModels { viewModelFactory }
    private val args: AdditionalInfoScreenFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_additional_info_screen, container, false
        )
        binding.lifecycleOwner = this
        val thisContext: Context = container?.context!!


        binding.viewModel = viewModel

        val day = args.day

        viewModel.forecast.observe(viewLifecycleOwner) { data ->
            viewModel.dayValue.value = day
            val forecastList: List<ForecastItem>? = data?.list?.filter { it -> it.date.contains(day) }
            if (forecastList != null) {
                setupRecyclerView(thisContext, forecastList)
            }
        }

        return binding.root
    }

    private fun setupRecyclerView(context: Context, forecast: List<ForecastItem>) {
        binding.recyclerForecastDetailed.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerForecastDetailed.adapter = ForecastDetailsAdapter(forecast)
    }

}
