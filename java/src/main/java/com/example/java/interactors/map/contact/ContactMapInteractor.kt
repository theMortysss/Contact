package com.example.java.interactors.map.contact

import com.example.java.entities.LocatedContact
import com.example.java.entities.ShortContact

interface ContactMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?

    suspend fun updateContactLocation(locatedContact: LocatedContact)

    suspend fun getShortContactById(contactId: String): ShortContact?

    suspend fun addContactLocation(locatedContact: LocatedContact)

    suspend fun reverseGeocoding(
        latitude: Double,
        longitude: Double,
        apikey: String
    ): String
}
