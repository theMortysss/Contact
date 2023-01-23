package com.example.java.interfaces

import com.example.java.entities.LocatedContact
import com.example.java.entities.LocationData

interface ILocationRepository {

    suspend fun getLocationData(contactId: String): LocationData?

    suspend fun getLocatedContactList(): List<LocatedContact>?

    suspend fun addContactLocation(locatedContact: LocatedContact)

    suspend fun updateContactLocation(locatedContact: LocatedContact)

    suspend fun deleteContactLocation(contactId: String)
}
