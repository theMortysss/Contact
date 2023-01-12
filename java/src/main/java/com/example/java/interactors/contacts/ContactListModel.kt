package com.example.java.interactors.contacts

import com.example.java.entities.ShortContact
import com.example.java.interfaces.IContactsRepository

class ContactListModel(private val repository: IContactsRepository) :
    ContactListInteractor {

    override suspend fun getContacts(query: String): List<ShortContact> =
        repository.getContacts(query)
}