package com.example.library.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java.entities.ShortContact
import com.example.java.interactors.contacts.ContactListInteractor
import com.example.library.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactListViewModel @Inject constructor(
    private val repository: ContactListInteractor
) : ViewModel() {

    private lateinit var contactList: List<ShortContact>
    private val filteredList = MutableLiveData<List<ShortContact>>()

    fun getLocatedContactList(query: String): MutableLiveData<List<ShortContact>> = loadContactList(query)

    private fun loadContactList(query: String): MutableLiveData<List<ShortContact>> {
        Log.d(TAG, "ContactListViewModel начинаю запрос данных контактов...")
        viewModelScope.launch(Dispatchers.IO) {
            contactList = repository.getContacts(query)
            filteredList.postValue(contactList)
            Log.d(TAG, "ContactListViewModel данные контактов получены.")
        }
        return filteredList
    }

    init {
        loadContactList(EMPTY_QUERY)
    }
    companion object {
        const val EMPTY_QUERY = ""
    }
}
