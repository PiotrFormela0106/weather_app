package com.example.weatherapp.ui.additionalinfo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAdditionalInfoScreenBinding
import com.example.weatherapp.di.DaggerMainScreenComponent
import com.example.weatherapp.di.RepositoryModule
import com.example.weatherapp.domain.models.ForecastItem
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.ui.main.ForecastAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AdditionalInfoScreenFragment : Fragment() {
    private lateinit var binding: FragmentAdditionalInfoScreenBinding

    @Inject
    lateinit var viewModel: AdditionalInfoScreenViewModel
    private val args: AdditionalInfoScreenFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_additional_info_screen, container, false
        )
        binding.lifecycleOwner = this
        val thisContext: Context = container?.context!!
        DaggerMainScreenComponent.builder()
            .repositoryModule(RepositoryModule(thisContext))
            .build()
            .inject(this)

        binding.viewModel = viewModel

        val day = args.day

        viewModel.forecast.observe(viewLifecycleOwner) { data ->
            viewModel.dayValue.value = day
            val forecastList: List<ForecastItem>? = data?.list?.filter { it -> it.date.contains(day)}
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