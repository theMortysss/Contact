package com.example.library.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.entities.LocatedContact
import com.example.java.interactors.map.everybody.EverybodyMapInteractor
import com.example.library.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EverybodyMapViewModel @Inject constructor(
    private val interactor: EverybodyMapInteractor
) : ViewModel() {

    private val locatedContactList = MutableLiveData<List<LocatedContact>>()

    init {
        loadLocatedContactList()
    }

    fun getLocatedContactList(): MutableLiveData<List<LocatedContact>> = loadLocatedContactList()

    private fun loadLocatedContactList(): MutableLiveData<List<LocatedContact>> {
        Log.d(TAG, "EverybodyMapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch(Dispatchers.IO) {
            locatedContactList.postValue(interactor.getLocatedContactList())
            Log.d(TAG, "EverybodyMapViewModel: координаты контактов получены.")
        }
        return locatedContactList
    }
}
