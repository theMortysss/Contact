package com.example.java.interactors.contacts

import com.example.java.entities.ShortContact

interface ContactListInteractor {

    suspend fun getContacts(query: String): List<ShortContact>
}
