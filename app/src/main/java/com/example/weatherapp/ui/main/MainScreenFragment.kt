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
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
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
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class MainScreenFragment : DaggerFragment(), LifecycleObserver, DefaultLifecycleObserver {
    private lateinit var binding: FragmentMainScreenBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainScreenViewModel by viewModels { viewModelFactory }
    lateinit var forecast: ForecastWeather
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // We shouldn't change language programmatically. It should work automatically with
        // the language from global settings of your phone.
        // But on the app starts we really need to know language to setup lang in API call.
        // So indeed it should be the first step.
        // But this should be triggered in viewModel. But you do it twice, in fragment and in viewmodel
        setLang(viewModel.storageRepository.getLanguage().toData())

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
        } else {
            //check that viewModel.storageRepository.getPhotoId()!=null and is not empty
            Picasso.get()
                .load(viewModel.storageRepository.getPhotoId())
                .into(binding.cityImage)
        }
        getDetailedForecast()

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
    private fun getLocation() { // move all that you can from this method to viewmodel
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location: Location = task.result
            try {
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

    // This function has wrong name.
    // It setups recycler view. Please rename function.
    private fun getDetailedForecast() {
        val recyclerView = binding.recyclerForecast
        binding.recyclerForecast.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        viewModel.forecastData.observe(viewLifecycleOwner) { data ->

                            //I don't understand these calculations. Please explain.
                            //But anyway
                            //every "removeRange" can be a place of IndexOutOfBoundsException.
                            // Please handle possible exception.
                            val forecastWithUniqueDays = data.list.distinctBy {
                                it.date.removeRange(10, 19).removeRange(0, 5)
                            }
                            val listOfDays: MutableList<String> = mutableListOf()
                            for (day in forecastWithUniqueDays) {
                                listOfDays.add(day.date.removeRange(10, 19).removeRange(0, 5))
                            }
                            val arrayList: Array<String> = listOfDays.toTypedArray()
                            val textView = view?.findViewById<TextView>(R.id.day)
                            findNavController().navigate(
                                MainScreenFragmentDirections.navigateToViewPager(
                                    textView?.text.toString(),
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
        binding.recyclerForecast.adapter = ForecastAdapter(forecast)
    }

    private fun setLang(lang: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(lang)
        resources.updateConfiguration(configuration, metrics)
        onConfigurationChanged(configuration)
    }

    private fun handleEvent(event: MainScreenViewModel.Event) {
        when (event) {
            is MainScreenViewModel.Event.OnCitiesClick -> {// extract fun goToLocationScreen()
                if (findNavController().currentDestination?.id == R.id.mainScreenFragment)
                    findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
            }
            is MainScreenViewModel.Event.OnSettingsClick -> {// extract fun openSettingsSheet()
                if (findNavController().currentDestination?.id == R.id.mainScreenFragment)
                    findNavController().navigate(MainScreenFragmentDirections.navigateToSettings())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {//this is not used now
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {// this is not used now too
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
