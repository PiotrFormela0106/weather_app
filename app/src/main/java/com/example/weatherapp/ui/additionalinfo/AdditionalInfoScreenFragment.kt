package com.example.weatherapp.ui.additionalinfo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.Cache
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAdditionalInfoScreenBinding
import com.example.weatherapp.di.DaggerMainScreenComponent
import com.example.weatherapp.di.RepositoryModule
import javax.inject.Inject

class AdditionalInfoScreenFragment : Fragment() {
    private lateinit var binding: FragmentAdditionalInfoScreenBinding
    @Inject
    lateinit var viewModel: AdditionalInfoScreenViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_additional_info_screen, container, false
        )
        binding.lifecycleOwner = this
        val thisContext: Context = container?.context!!
        DaggerMainScreenComponent.builder()
            .repositoryModule(RepositoryModule(thisContext))
            .build()
            .inject(this)

        binding.viewModel = viewModel

        return binding.root
    }

}