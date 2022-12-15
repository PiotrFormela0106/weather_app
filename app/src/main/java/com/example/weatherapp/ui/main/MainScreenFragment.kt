package com.example.weatherapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.databinding.FragmentMainScreenBinding
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.ui.core.RecyclerItemClickListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainScreenFragment : Fragment(), LifecycleObserver, DefaultLifecycleObserver {
    private lateinit var binding: FragmentMainScreenBinding

    private val viewModel by viewModels<MainScreenViewModel>()
    lateinit var forecast: ForecastWeather
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_screen, container, false
        )
        binding.viewState = viewModel.ViewState()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        lifecycle.addObserver(viewModel)

        viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleEvent(it) }

        setHasOptionsMenu(true)

        viewModel.forecastData.observe(viewLifecycleOwner) { data ->
            forecast = data
            setupRecyclerView(requireContext(), forecast)
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (viewModel.storageRepository.getLocationMethod() == LocationMethod.Location) {
            checkPermission()
        } else if (viewModel.storageRepository.getLocationMethod() == LocationMethod.City) {
            if (viewModel.storageRepository.getPhotoId().isNotEmpty()) {
                Picasso.get()
                    .load(viewModel.storageRepository.getPhotoId())
                    .into(binding.cityImage)
                viewModel.photoVisibility.value = true
            } else {
                viewModel.photoVisibility.value = false
            }
        }
        setupForecastRecyclerViewOnClick()

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
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    checkGPS()
                }
            } else {
                alert(
                    "Allow permission for location to get weather in your region or select city",
                    "Location permission denied"
                )
            }
        }
    }

    private fun alert(message: String, title: String) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton(
                "ok"
            ) { _, _ ->
                findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
            }
        }
            .create()
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            try {
                val location: Location = task.result
                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                )
                viewModel.storageRepository.saveCoordinates(
                    addresses[0].latitude,
                    addresses[0].longitude
                )
                viewModel.fetchData()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun checkGPS() {
        val manager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alert("You need to have Location Services enabled!", "Location GPS is disabled")
        } else {
            getLocation()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // When permission denied at start
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            checkGPS()
        }
    }

    private fun setupForecastRecyclerViewOnClick() {
        val recyclerView = binding.recyclerForecast
        binding.recyclerForecast.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                        viewModel.forecastData.observe(viewLifecycleOwner) { data ->
                            val forecastWithUniqueDays = data.list.distinctBy {
                                try {
                                    it.date.removeRange(10, 19).removeRange(0, 5)
                                } catch (e: IndexOutOfBoundsException) {
                                    Log.e("error", e.toString())
                                }
                            }
                            val listOfDays: MutableList<String> = mutableListOf()
                            for (day in forecastWithUniqueDays) {
                                try {
                                    listOfDays.add(day.date.removeRange(10, 19).removeRange(0, 5))
                                } catch (e: IndexOutOfBoundsException) {
                                    Log.e("error", e.toString())
                                }
                            }
                            val arrayList: Array<String> = listOfDays.toTypedArray()
                            val day = view?.findViewById<TextView>(R.id.day)?.text.toString()
                            var spf = SimpleDateFormat("dd MMM HH:mm", Locale(viewModel.storageRepository.getLanguage().toData()))
                            val newDate = spf.parse(day)
                            spf = SimpleDateFormat("MM-dd")
                            val formattedDay = spf.format(newDate)
                            findNavController().navigate(
                                MainScreenFragmentDirections.navigateToViewPager(
                                    day,
                                    arrayList
                                )
                            )
                        }
                    }
                }
            )
        )
    }

    private fun setupRecyclerView(context: Context, forecast: ForecastWeather) {
        binding.recyclerForecast.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerForecast.adapter = ForecastAdapter(forecast, viewModel.storageRepository, resources)
    }

    private fun handleEvent(event: MainScreenViewModel.Event) {
        when (event) {
            is MainScreenViewModel.Event.OnCitiesClick -> {
                goToLocationScreen()
            }
            is MainScreenViewModel.Event.OnSettingsClick -> {
                openSettingsSheet()
            }
        }
    }

    private fun openSettingsSheet() {
        if (findNavController().currentDestination?.id == R.id.mainScreenFragment)
            findNavController().navigate(MainScreenFragmentDirections.navigateToSettings())
    }

    private fun goToLocationScreen() {
        if (findNavController().currentDestination?.id == R.id.mainScreenFragment)
            findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 44
    }
}
