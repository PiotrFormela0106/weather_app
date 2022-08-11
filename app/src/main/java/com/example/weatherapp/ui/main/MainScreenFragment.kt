package com.example.weatherapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMainScreenBinding
import com.example.weatherapp.di.DaggerMainScreenComponent
import com.example.weatherapp.di.RepositoryModule
import javax.inject.Inject


class MainScreenFragment : Fragment(), LifecycleObserver, DefaultLifecycleObserver {
    private lateinit var binding: FragmentMainScreenBinding
    @Inject
    lateinit var viewModel: MainScreenViewModel

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

        binding.mainScreen = viewModel
        binding.mainScreenFragment = this
        binding.lifecycleOwner = this
        lifecycle.addObserver(viewModel)

        viewModel.getCurrentWeatherForCity()//move to init block of viewmodel
        //viewModel.getCurrentWeatherForLocation()
        viewModel.getForecastForCity()//move to init block of viewmodel
        viewModel.status.observe(viewLifecycleOwner, Observer<Boolean> { status ->
            if (!status) {
                Toast.makeText(activity, "There is no such city!", Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }


    fun goToCities(){//this logic should be moved to view model
        // onClick of databinding should call a function of viewmodel
        // and viewmodel should somehow pass this event to fragment
        // usually we have the source of events in viewmodel
        //and fragment subscribes on this events
        //
        //look at the sample app events() of viewModel
        // and fun handleEvents(event: Event) in fragment
        //
        // after you fix this,
        // you should remove "mainScreenFragment" from xml
        //and
        //in fun handleError of viewmodel send event from viewModel to fragment to show toast.
        //so you will be able to remove from fragment line "viewModel.status.observe..."
        val action = MainScreenFragmentDirections.navigateToCities()
        findNavController().navigate(action)
    }

}