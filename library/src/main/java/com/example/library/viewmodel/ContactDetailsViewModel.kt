package com.example.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.entities.Contact
import com.example.java.entities.LocationData
import com.example.library.utils.Constants.TAG
import com.example.java.interactors.contact.ContactDetailsInteractor
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(
    private val interactor: ContactDetailsInteractor
) : ViewModel() {

    private lateinit var contact: List<Contact>
    private val mutableContactDetails = MutableLiveData<List<Contact>>()
    private val mutableLocationData = MutableLiveData<LocationData>()
    private val contactDetails = mutableContactDetails as LiveData<List<Contact>>
    private val contactLocation = mutableLocationData as LiveData<LocationData>

    fun getContactDetails(contactId: String): LiveData<List<Contact>> {
        loadContactDetails(contactId)
        return contactDetails
    }

    fun getContactLocation(contactId: String): LiveData<LocationData> {
        loadLocationData(contactId)
        return contactLocation
    }

    fun deleteLocationData(contactId: String) {
        viewModelScope.launch {
            interactor.deleteLocationData(contactId)
        }
    }

    fun isBirthdayAlarmOn(curContact: Contact): Boolean =
        interactor.isBirthdayAlarmOn(curContact)

    fun setBirthdayAlarm(curContact: Contact) =
        viewModelScope.launch {
            interactor.setBirthdayAlarm(curContact)
        }

    fun cancelBirthdayAlarm(curContact: Contact) =
        viewModelScope.launch {
            interactor.cancelBirthdayAlarm(curContact)
        }

    private fun loadLocationData(contactId: String) {
        viewModelScope.launch {
            mutableLocationData.postValue(interactor.getLocationData(contactId))
        }
    }

    private fun loadContactDetails(contactId: String) {
        Log.d(TAG, "ContactDetailsViewModel начинаю запрос данных контакта...")
        viewModelScope.launch {
            contact = interactor.getContactDetails(contactId)
            mutableContactDetails.postValue(contact)
            Log.d(TAG, "ContactDetailsViewModel данные контакта получены.")
        }
    }

}