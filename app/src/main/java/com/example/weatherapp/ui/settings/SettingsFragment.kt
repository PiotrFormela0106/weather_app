package com.example.weatherapp.ui.settings

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.toData
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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

        viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleEvent(it) }

        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setLang(viewModel.storageRepository.getLanguage().toData())
        findNavController().navigate(SettingsFragmentDirections.navigateToMainScreen())
    }

    private fun setLang(lang: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(lang)
        resources.updateConfiguration(configuration, metrics)
        onConfigurationChanged(configuration)
    }

    private fun handleEvent(event: SettingsViewModel.Event) {
        when (event) {
            is SettingsViewModel.Event.OnCancelClick -> {
                findNavController().navigate(SettingsFragmentDirections.navigateToMainScreen())
            }
        }
    }
}
