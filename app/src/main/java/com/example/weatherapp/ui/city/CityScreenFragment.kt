package com.example.weatherapp.ui.city

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.room.City
import com.example.weatherapp.databinding.FragmentCityScreenBinding
import com.example.weatherapp.di.DaggerCityScreenComponent
import com.example.weatherapp.di.RepositoryModule
import com.example.weatherapp.ui.CityAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class CityScreenFragment : Fragment() {
    private lateinit var binding: FragmentCityScreenBinding
    private lateinit var adapter: CityAdapter


    @Inject
    lateinit var viewModel: CityScreenViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_city_screen, container, false
        )
        val thisContext: Context = container?.context!!

        DaggerCityScreenComponent.builder()
            .repositoryModule(RepositoryModule(thisContext))
            .build()
            .inject(this)

        binding.viewModel = viewModel

        viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleEvent(it) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllCities()
        viewModel.allCities.observe(viewLifecycleOwner, Observer<List<City>> { data ->
            setupRecyclerView(data)

            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    //Toast.makeText(activity, "on Move", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    //Toast.makeText(activity, "on Swiped ", Toast.LENGTH_SHORT).show()
                    val position = viewHolder.adapterPosition
                    viewModel.deleteCity(data[position])
                    adapter.notifyDataSetChanged()
                }
            }

            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(binding.recyclerCity)

        })

    }

    private fun setupRecyclerView(list: List<City>) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCity.layoutManager = layoutManager
        adapter = CityAdapter(list)
        binding.recyclerCity.adapter = adapter
    }

    private fun handleEvent(event: CityScreenViewModel.Event) {
        when (event) {
            is CityScreenViewModel.Event.OnAddCity -> {
                val city = City(binding.inputCity.text.toString())
                viewModel.checkCity(city)
                viewModel.allCities.observe(viewLifecycleOwner, Observer<List<City>> { data ->
                    adapter = CityAdapter(data)
                    binding.recyclerCity.adapter = adapter
                })
            }

            is CityScreenViewModel.Event.OnRemoveCities -> {
                viewModel.deleteAllCities()
                viewModel.allCities.observe(viewLifecycleOwner, Observer<List<City>> { data ->
                    adapter = CityAdapter(data)
                    binding.recyclerCity.adapter = adapter
                })
            }
            is CityScreenViewModel.Event.OnCityError -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show();
            }

        }
    }
}