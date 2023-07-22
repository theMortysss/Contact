package com.example.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.entities.LocatedContact
import com.example.java.entities.LocationData
import com.example.java.entities.ShortContact
import com.example.java.interactors.map.contact.ContactMapInteractor
import com.example.library.utils.Constants.GEOCODER_API_KEY
import com.example.library.utils.Constants.TAG
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class ContactMapViewModel @Inject constructor(
    private val interactor: ContactMapInteractor
) : ViewModel() {

    private val yandexGeocoderApiKey = GEOCODER_API_KEY
    private val changedLocationData = MutableLiveData<LocationData?>()
    private var shortContact = MutableLiveData<ShortContact?>()
    private val locatedContactList = MutableLiveData<MutableList<LocatedContact>>()

    init {
        loadLocatedContactList()
    }

    @Suppress("UNCHECKED_CAST")
    fun getLocatedContactList(): LiveData<List<LocatedContact>> =
        locatedContactList as LiveData<List<LocatedContact>>

    fun getShortContact(contactId: String): LiveData<ShortContact?> {
        loadShortContact(contactId)
        return shortContact
    }

    fun getChangedLocationData(): LiveData<LocationData?> = changedLocationData

    fun setChangedLocationData(locationData: LocationData) {
        changedLocationData.value = locationData
        reverseGeocoding(locationData.latitude, locationData.longitude, yandexGeocoderApiKey)
        Log.d(TAG, "ContactMapViewModel: изменение положения маркера записано")
    }

    fun resetChangedLocationData() {
        changedLocationData.value = null
    }

    fun addLocatedContact(locatedContact: LocatedContact) {
        viewModelScope.launch {
            locatedContactList.value?.add(locatedContact)
            interactor.addContactLocation(locatedContact)
        }
    }

    fun updateLocatedContact(locatedContact: LocatedContact) {
        viewModelScope.launch {
            val list = locatedContactList.value
            if (list != null) {
                locatedContactList.value?.set(
                    list.indexOfFirst { it.id == locatedContact.id },
                    locatedContact
                )
                interactor.updateContactLocation(locatedContact)
            }
        }
    }

    private fun loadLocatedContactList() {
        Log.d(TAG, "ContactMapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.postValue(
                (interactor.getLocatedContactList() ?: emptyList())
                    as MutableList<LocatedContact>
            )
            Log.d(TAG, "ContactMapViewModel: координаты контактов получены.")
        }
    }

    private fun loadShortContact(contactId: String) {
        viewModelScope.launch {
            shortContact.postValue(interactor.getShortContactById(contactId))
        }
    }

    private fun reverseGeocoding(
        latitude: Double,
        longitude: Double,
        apikey: String
    ) {
        viewModelScope.launch {
            try {
                changedLocationData.value =
                    changedLocationData.value?.copy(
                        address = interactor.reverseGeocoding(
                            latitude = latitude,
                            longitude = longitude,
                            apikey = apikey
                        )
                    )
            } catch (e: IOException) {
                Log.d(TAG, e.stackTraceToString())
                changedLocationData.value =
                    changedLocationData.value?.copy(
                        address = "Адрес не найден"
                    )
            }
        }
    }


}
