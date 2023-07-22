package com.example.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.interactors.options.OptionsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class OptionsViewModel @Inject constructor(
    val dataStore: OptionsInteractor
) : ViewModel() {

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStore.setDarkMode(enabled)
    }
}
