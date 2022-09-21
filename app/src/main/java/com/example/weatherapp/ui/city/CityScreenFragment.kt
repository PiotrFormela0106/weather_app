package com.example.weatherapp.ui.city

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.BuildConfig.PLACES_API_KEY
import com.example.weatherapp.R
import com.example.weatherapp.data.room.City
import com.example.weatherapp.databinding.FragmentCityScreenBinding
import com.example.weatherapp.domain.models.LocationMethod
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CityScreenFragment : DaggerFragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentCityScreenBinding
    private lateinit var adapter: GridAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: CityScreenViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_city_screen, container, false
        )

        binding.viewModel = viewModel

        viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleEvent(it) }

        setHasOptionsMenu(true)
        setupAutocompleteSearchFragment()

        // val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        // mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allCities.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                val reversedList = data.reversed().toMutableList()
                setupGridView(list = reversedList)
            } else
                setupGridView(data)

            binding.gridViewCity.onItemClickListener =
                AdapterView.OnItemClickListener { _, view, _, _ ->

                    viewModel.storageRepository.saveLocationMethod(LocationMethod.City)
                    val textView = view?.findViewById<TextView>(R.id.cityName)
                    viewModel.storageRepository.saveCity(textView?.text.toString())
                    viewModel.getPhotoId(textView?.text.toString())
                    viewModel.photoId.observe(viewLifecycleOwner) { id ->
                        viewModel.storageRepository.savePhotoId(id)
                        findNavController().navigate(CityScreenFragmentDirections.navigateToMainScreen())
                    }
                }
        }

        binding.pickPlace.setOnClickListener {
        }
    }

    private fun setupAutocompleteSearchFragment() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), PLACES_API_KEY)
        }
        val autocompleteFragment: AutocompleteSupportFragment = childFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHOTO_METADATAS
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.i("error", "An error occurred: $status")
            }

            override fun onPlaceSelected(place: Place) {
                if (place.photoMetadatas != null) {
                    val photoId = place.photoMetadatas?.first()?.zza()
                    val url = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "maxwidth=400&" +
                        "photo_reference=$photoId" +
                        "&" + "key=$PLACES_API_KEY"
                    viewModel.addCity(place, photoId = url)
                } else {
                    val url =
                        "https://previews.123rf.com/images/dvarg/dvarg1402/dvarg140200058/25942935-city-map-booklet-with-question-mark-on-white-background.jpg"
                    viewModel.addCity(place, url)
                }
            }
        })
    }

    private fun setupGridView(list: List<City>) {
        adapter = GridAdapter(list, requireContext())
        binding.gridViewCity.adapter = adapter
    }

    private fun handleEvent(event: CityScreenViewModel.Event) {
        when (event) {
            is CityScreenViewModel.Event.OnAddCity -> {
                goToMainScreen()
            }
            is CityScreenViewModel.Event.OnBack -> {
                findNavController().popBackStack()
            }
            is CityScreenViewModel.Event.OnCityError -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is CityScreenViewModel.Event.OnLocation -> {
                goToMainScreen()
            }
            is CityScreenViewModel.Event.OnMaps -> {
                openMap()
            }
        }
    }

    private fun openMap() {
        if (findNavController().currentDestination?.id == R.id.cityScreenFragment)
            findNavController().navigate(CityScreenFragmentDirections.navigateToMapsScreen())
    }

    private fun goToMainScreen() {
        if (findNavController().currentDestination?.id == R.id.cityScreenFragment)
            findNavController().navigate(CityScreenFragmentDirections.navigateToMainScreen())
    }

    companion object {
        private const val PLACE_PICKER_REQUEST = 1
    }

    override fun onMapReady(p0: GoogleMap) {
    }
}
