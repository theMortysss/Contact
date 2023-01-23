package com.example.java.interfaces

import com.example.java.entities.Contact
import com.example.java.entities.ShortContact

interface IContactsRepository {
    suspend fun getContacts(query: String): List<ShortContact>
    suspend fun getContactDetails(contactId: String): List<Contact>
    suspend fun getShortContact(contactId: String): ShortContact?
}
