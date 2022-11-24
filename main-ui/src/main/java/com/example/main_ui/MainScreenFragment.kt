package com.example.main_ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.fragment.findNavController
import com.example.base.ui.theme.AppTheme
import com.example.main_ui.ui.MainScreen
import com.example.weather_domain.models.ForecastWeather
import com.example.weather_domain.models.LocationMethod
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class MainScreenFragment : Fragment(), LifecycleObserver, DefaultLifecycleObserver {
    private val viewModel by viewModels<MainScreenViewModel>()
    lateinit var forecast: ForecastWeather
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycle.addObserver(viewModel)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (viewModel.storageRepository.getLocationMethod() == LocationMethod.Location) {
            checkPermission()
        } else if (viewModel.storageRepository.getLocationMethod() == LocationMethod.City) {
            viewModel.photoVisibility.value = viewModel.storageRepository.getPhotoId().isNotEmpty()
        }

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        MainScreen(viewModel, navController = findNavController())
                    }
                }
            }
        }
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

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 44
    }
}
