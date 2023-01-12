package com.example.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.entities.LocatedContact
import com.example.library.utils.Constants.TAG
import com.example.java.interactors.map.route.RouteMapInteractor
import kotlinx.coroutines.launch
import javax.inject.Inject

class RouteMapViewModel @Inject constructor(
    private val interactor: RouteMapInteractor
) : ViewModel() {

    private val locatedContactList = MutableLiveData<List<LocatedContact>>()

    init {
        loadLocatedContactList()
    }

    fun getLocatedContactList() : LiveData<List<LocatedContact>> = locatedContactList

    private fun loadLocatedContactList() {
        Log.d(TAG, "RouteMapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.value = interactor.getLocatedContactList()
            Log.d(TAG, "RouteMapViewModel: координаты контактов получены.")
        }
    }
}