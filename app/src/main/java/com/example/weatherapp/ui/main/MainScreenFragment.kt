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
import com.example.weatherapp.BuildConfig.PLACES_API_KEY
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.databinding.FragmentMainScreenBinding
import com.example.weatherapp.domain.models.ForecastWeather
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.ui.core.RecyclerItemClickListener
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
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

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), PLACES_API_KEY)
        }
        val placesClient = Places.createClient(requireContext())
        val fields = listOf(Place.Field.PHOTO_METADATAS)
        val placeId = viewModel.storageRepository.getPlaceId()
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                val metaData = place.photoMetadatas
                if (metaData == null || metaData.isEmpty()) {
                    Log.w("NO PHOTO", "No photo metadata.")
                    return@addOnSuccessListener
                }
                val photoMetadata = metaData.first()
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    // .setMaxWidth(1000) // Optional.
                    // .setMaxHeight(400) // Optional.
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        binding.cityImage.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("Place not found", "Place not found: " + exception.message)
                        }
                    }
            }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (viewModel.storageRepository.getLocationMethod() == LocationMethod.Location) {
            checkPermission()
        }
        getDetailedForecast()

        return binding.root
    }

    override fun onResume() {
        super<DaggerFragment>.onResume()
        setLang(viewModel.storageRepository.getLanguage().toData())
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

    private fun getDetailedForecast() {
        val recyclerView = binding.recyclerForecast
        binding.recyclerForecast.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val textView = view?.findViewById<TextView>(R.id.day)
                        findNavController().navigate(
                            MainScreenFragmentDirections.navigateToAddInfo(
                                textView?.text.toString()
                            )
                        )
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
            is MainScreenViewModel.Event.OnCitiesClick -> {
                if (findNavController().currentDestination?.id == R.id.mainScreenFragment)
                    findNavController().navigate(MainScreenFragmentDirections.navigateToCities())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
