package com.example.java.interactors.map.contact

import com.example.java.entities.LocatedContact
import com.example.java.entities.ShortContact
import com.example.java.interfaces.IContactsRepository
import com.example.java.interfaces.ILocationRepository
import com.example.java.interfaces.IYandexGeocoderRepository

class ContactMapModel(
    private val locationRepository: ILocationRepository,
    private val contactsRepository: IContactsRepository,
    private val geocoderRepository: IYandexGeocoderRepository
) : ContactMapInteractor {

    override suspend fun getShortContactById(contactId: String): ShortContact? =
        contactsRepository.getShortContact(contactId)

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
        locationRepository.getLocatedContactList()

    override suspend fun updateContactLocation(locatedContact: LocatedContact) =
        locationRepository.updateContactLocation(locatedContact)

    override suspend fun addContactLocation(locatedContact: LocatedContact) =
        locationRepository.addContactLocation(locatedContact)

    override suspend fun reverseGeocoding(
        latitude: Double,
        longitude: Double,
        apikey: String
    ): String =
        geocoderRepository.reverseGeocoding(
            latitude = latitude,
            longitude = longitude,
            apikey = apikey
        )
}
