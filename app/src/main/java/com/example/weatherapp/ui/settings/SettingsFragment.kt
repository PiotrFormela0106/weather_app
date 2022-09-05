package com.example.weatherapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingsBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

class SettingsFragment : DaggerFragment() {
    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
