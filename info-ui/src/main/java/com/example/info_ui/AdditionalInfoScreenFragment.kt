package com.example.info_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.base.ui.theme.AppTheme
import com.example.info_ui.ui.ContentView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdditionalInfoScreenFragment : Fragment() {

    private val viewModel by viewModels<AdditionalInfoScreenViewModel>()
    private val args: AdditionalInfoScreenFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val days = args.days

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        ContentView(viewModel = viewModel, days = days, activity = activity, args = args)
                    }
                }
            }
        }
    }
}
