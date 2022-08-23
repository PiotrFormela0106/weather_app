package com.example.weatherapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMainScreenBinding
import com.example.weatherapp.di.DaggerMainScreenComponent
import com.example.weatherapp.di.RepositoryModule
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.ui.ForecastAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class MainScreenFragment : Fragment(), LifecycleObserver, DefaultLifecycleObserver {
    private lateinit var binding: FragmentMainScreenBinding
    @Inject
    lateinit var viewModel: MainScreenViewModel
    lateinit var forecast: ForecastWeather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_screen, container, false
        )

        val thisContext: Context = container?.context!!
        DaggerMainScreenComponent.builder()
            .repositoryModule(RepositoryModule(thisContext))
            .build()
            .inject(this)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        lifecycle.addObserver(viewModel)

        viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{handleEvent(it)}

        //-----------IN-PROGRESS-------------//
        viewModel.forecastData.observe(viewLifecycleOwner,Observer<ForecastWeather>{data ->
            forecast = data
            setupRecyclerView(thisContext, forecast)
        })
        //-----------IN-PROGRESS-------------//

        return binding.root
    }

    private fun setupRecyclerView(context: Context, forecast: ForecastWeather){
        binding.recyclerForecast.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerForecast.adapter = ForecastAdapter(forecast)
    }

    private fun handleEvent(event: MainScreenViewModel.Event) {
        when(event){
            is MainScreenViewModel.Event.OnCitiesClick -> {
                findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
            }
            is MainScreenViewModel.Event.OnCityError -> {
                Toast.makeText(activity, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }



}