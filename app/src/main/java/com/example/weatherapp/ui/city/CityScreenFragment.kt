package com.example.weatherapp.ui.city

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.room.City
import com.example.weatherapp.databinding.FragmentCityScreenBinding
import com.example.weatherapp.di.DaggerCityScreenComponent
import com.example.weatherapp.di.RepositoryModule
import com.example.weatherapp.domain.models.LocationMethod
import com.example.weatherapp.ui.core.RecyclerItemClickListener
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

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateCities()
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
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.deleteCity(data[position])
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(activity, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
                }

            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(binding.recyclerCity)
        })

        val recyclerView = binding.recyclerCity
        binding.recyclerCity.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        viewModel.storageRepository.saveLocationMethod(LocationMethod.City)
                        val textView = view?.findViewById<TextView>(R.id.cityName)
                        viewModel.storageRepository.saveCity(textView?.text.toString())
                        findNavController().navigate(CityScreenFragmentDirections.navigateToMainScreen())

                    }

                })
        )

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
                findNavController().navigate(CityScreenFragmentDirections.navigateToMainScreen())
            }
            is CityScreenViewModel.Event.OnCityError -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show();
            }
            is CityScreenViewModel.Event.OnCityDuplicate -> {
                Snackbar.make(
                    binding.root,
                    "This city is already added to list",
                    Snackbar.LENGTH_SHORT
                ).show();
            }
            is CityScreenViewModel.Event.OnLocation -> {
                findNavController().navigate(CityScreenFragmentDirections.navigateToMainScreen())
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_bin, menu)
        super.onCreateOptionsMenu(menu,inflater);
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bin -> {
                viewModel.deleteAllCities()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}