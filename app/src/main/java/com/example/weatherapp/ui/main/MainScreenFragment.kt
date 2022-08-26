package com.example.weatherapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.databinding.FragmentMainScreenBinding
import com.example.weatherapp.di.DaggerMainScreenComponent
import com.example.weatherapp.di.RepositoryModule
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.ui.ForecastAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.util.*
import javax.inject.Inject


class MainScreenFragment : Fragment(), LifecycleObserver, DefaultLifecycleObserver {
    private lateinit var binding: FragmentMainScreenBinding
    @Inject
    lateinit var viewModel: MainScreenViewModel
    lateinit var forecast: ForecastWeather
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient



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

        setHasOptionsMenu(true)

        viewModel.forecastData.observe(viewLifecycleOwner,Observer<ForecastWeather>{data ->
            forecast = data
            setupRecyclerView(thisContext, forecast)
        })

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //Check permission
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //When permission denied at start
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            //When permission granted
            if(viewModel.storageRepository.getLocationMethod()== LocationMethod.Location)
                getLocation()
        }

        return binding.root
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // Checking whether user granted the permission or not.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                viewModel.storageRepository.saveLocationMethod(LocationMethod.Location)
                Toast.makeText(requireContext(), "Location Permission Granted", Toast.LENGTH_SHORT)
                    .show()
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getLocation()
                }
            } else {
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setMessage("Allow permission for location to get weather in your region or select city")
                alertDialogBuilder.setTitle("Location permission denied")
                alertDialogBuilder.setNegativeButton(
                    "ok"
                ) { _, _ ->
                    findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            //Initialize location
            val location: Location = task.result
            if (location != null) {

                try {
                    val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                    //Initialize address list
                    val addresses: List<Address> = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1
                    )
                    viewModel.storageRepository.saveCoordinates(addresses[0].latitude,addresses[0].longitude)
                    viewModel.getCurrentWeather()
                    viewModel.getForecastWeather()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
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
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu,inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(MainScreenFragmentDirections.navigateToSettings())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 44
    }


}