package com.example.library.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.entities.LocatedContact
import com.example.library.utils.Constants.TAG
import com.example.java.interactors.map.everybody.EverybodyMapInteractor
import kotlinx.coroutines.launch
import javax.inject.Inject

class EverybodyMapViewModel @Inject constructor(
    private val interactor: EverybodyMapInteractor
) : ViewModel() {

    private val locatedContactList = MutableLiveData<List<LocatedContact>>()

    init {
        loadLocatedContactList()
    }

    fun getLocatedContactList(): MutableLiveData<List<LocatedContact>> = locatedContactList

    private fun loadLocatedContactList() {
        Log.d(TAG, "MapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.value = interactor.getLocatedContactList()
            Log.d(TAG, "MapViewModel: координаты контактов получены")
        }
    }
}