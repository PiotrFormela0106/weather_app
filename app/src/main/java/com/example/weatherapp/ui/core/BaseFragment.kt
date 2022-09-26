package com.example.weatherapp.ui.core

import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class BaseFragment : Fragment() {
    private val logTag = this.javaClass.simpleName

    var scope: CoroutineScope? = null

    fun initScope() {
        Log.d(logTag, "create new scope")
        scope = CoroutineScope(Dispatchers.Main)
    }

    override fun onDestroyView() {
        Log.d(logTag, "cancel scope")
        scope?.cancel()
        super.onDestroyView()
    }
}
