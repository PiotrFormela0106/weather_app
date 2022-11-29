package com.example.weatherapp.ui.additionalinfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.databinding.FragmentAdditionalInfoScreenBinding
import com.example.weatherapp.domain.models.ForecastItem
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AdditionalInfoScreenFragment(val day: String) : DaggerFragment() {
    private lateinit var binding: FragmentAdditionalInfoScreenBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: AdditionalInfoScreenViewModel by viewModels { viewModelFactory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_additional_info_screen, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleEvent(it) }

        val day = day

        viewModel.forecast.observe(viewLifecycleOwner) { data ->
            val lang = viewModel.storageRepository.getLanguage().toData()
            val forecastList = data?.list?.filter { it -> it.date.contains(day) }.orEmpty()
            val item = forecastList.first().dateLong * 1000
            val sdf = SimpleDateFormat("EEEE, d MMMM", Locale(lang))
            val dateAndTime = Date(item)
            val dateAndTimeFormat = sdf.format(dateAndTime)
            viewModel.dayValue.value = dateAndTimeFormat
            setupRecyclerView(requireContext(), forecastList)
        }

        return binding.root
    }

    private fun setupRecyclerView(context: Context, forecast: List<ForecastItem>) {
        binding.recyclerForecastDetailed.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerForecastDetailed.adapter =
            ForecastDetailsAdapter(forecast, requireContext())
    }
    private fun handleEvent(event: AdditionalInfoScreenViewModel.Event) {
        when (event) {
            is AdditionalInfoScreenViewModel.Event.OnBack -> {
                findNavController().popBackStack()
            }
        }
    }
}
