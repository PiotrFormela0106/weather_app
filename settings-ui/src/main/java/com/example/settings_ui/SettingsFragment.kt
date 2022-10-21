package com.example.settings_ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.settings_ui.databinding.FragmentSettingsBinding
import com.example.weather_domain.models.Language
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModels<SettingsViewModel>()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { handleEvent(it) }
        }

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

private fun Language.toData(): String {
    return when (this) {
        Language.ENG -> ENG
        Language.PL -> PL
        Language.DE -> DE
    }
}
private const val PL = "pl"
private const val ENG = "eng"
private const val DE = "de"
