package com.example.location_ui

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.base.BuildConfig.PLACES_API_KEY
import com.example.location_ui.databinding.FragmentMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding

    private val viewModel by viewModels<MapsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_maps, container, false
        )

        binding.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { handleEvent(it) }
        }

        setupAutocompleteSearchFragment()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val callback = OnMapReadyCallback { googleMap ->
            val lat = viewModel.storageRepository.getCoordinates().first
            val lon = viewModel.storageRepository.getCoordinates().second
            val lastLocation = LatLng(lat, lon)
            var markerOptions = MarkerOptions().position(lastLocation)
            var marker: Marker? = googleMap.addMarker(markerOptions)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 10F))
            viewModel.setLangLong(lat, lon)

            if (!Places.isInitialized()) {
                Places.initialize(requireContext(), PLACES_API_KEY)
            }
            val autocompleteFragment: AutocompleteSupportFragment = childFragmentManager
                .findFragmentById(R.id.autocomplete_fragment_map) as AutocompleteSupportFragment

            autocompleteFragment.setPlaceFields(
                listOf(
                    Place.Field.ID,
                    Place.Field.LAT_LNG,
                    Place.Field.NAME,
                    Place.Field.ADDRESS
                )
            )
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onError(status: Status) {
                    Log.i("error", "An error occurred: $status")
                }

                override fun onPlaceSelected(place: Place) {
                    if (place.latLng != null) {
                        val placeCoordinates = Pair(place.latLng?.latitude, place.latLng?.longitude)
                        val addresses = geocoder.getFromLocation(placeCoordinates.first!!, placeCoordinates.second!!, 1)
                        if (addresses != null) {
                            marker?.remove()
                            val mAddress = addresses[0].getAddressLine(0)
                            val placeLocation = LatLng(placeCoordinates.first!!, placeCoordinates.second!!)
                            markerOptions = MarkerOptions().position(placeLocation)
                            markerOptions.position(placeLocation).title(mAddress)
                            marker = googleMap.addMarker(markerOptions)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation))
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 10F))
                            viewModel.setLangLong(placeCoordinates.first!!, placeCoordinates.second!!)
                        }
                    }
                }
            })

            googleMap.setOnMapClickListener {
                marker?.remove()
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses != null) {
                    val mAddress = addresses[0].getAddressLine(0)
                    val location = LatLng(it.latitude, it.longitude)
                    markerOptions = MarkerOptions().position(location)
                    markerOptions.position(location).title(mAddress)
                    marker = googleMap.addMarker(markerOptions)
                    viewModel.setLangLong(it.latitude, it.longitude)
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupAutocompleteSearchFragment() {
    }

    private fun handleEvent(event: MapsViewModel.Event) {
        when (event) {
            is MapsViewModel.Event.OnPickPlace -> {
                findNavController().popBackStack(com.example.base.R.id.main_screen_id, false)
            }
            is MapsViewModel.Event.OnBack -> {
                findNavController().popBackStack()
            }
        }
    }
}
