package com.example.library.repository.location

import android.util.Log
import com.example.java.entities.LocatedContact
import com.example.java.entities.LocationData
import com.example.java.interfaces.IContactsRepository
import com.example.java.interfaces.ILocationRepository
import com.example.library.room.database.LocationDatabase
import com.example.library.room.entity.LocationEntity
import com.example.library.room.entity.toDatabase
import com.example.library.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(
    private val database: LocationDatabase,
    private val contactsRepositoryInterface: IContactsRepository
) : ILocationRepository {

    private var locationData : LocationData? = null

    override suspend fun getLocationData(contactId: String) : LocationData? {
        withContext(Dispatchers.IO) {
            locationData = database.locationDao().getContactLocation(contactId)?.let {
                LocationData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    address = it.address
                )
            }
        }
        return locationData
    }

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
        withContext(Dispatchers.IO) {
            database.locationDao().getAllContactLocations()?.mapNotNull {
                withContext(Dispatchers.IO) {
                    val contact =
                        contactsRepositoryInterface.getShortContact(it.id)
                    if (contact != null) {
                        LocatedContact(
                            id = it.id,
                            name = contact.name,
                            photoUri = contact.photoUri,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            address = it.address
                        )
                    }
                    else {
                        deleteContactLocation(it.id)
                        Log.d(TAG, "ContactLocationRepository: из БД удален "
                                + "несуществующий контакт id = ${it.id}"
                                + " address = ${it.address}")
                        null
                    }
                }
            }
        }

    override suspend fun addContactLocation(locatedContact: LocatedContact) {
        withContext(Dispatchers.IO) {
            database.locationDao().addContactLocation(locatedContact.toDatabase())
        }
    }

    override suspend fun updateContactLocation(locatedContact: LocatedContact) {
        withContext(Dispatchers.IO) {
            database.locationDao().updateContactLocation(
                    LocationEntity(
                        id = locatedContact.id,
                        latitude = locatedContact.latitude,
                        longitude = locatedContact.longitude,
                        address = locatedContact.address
                    )
                )
        }
    }

    override suspend fun deleteContactLocation(contactId: String) {
        withContext(Dispatchers.IO) {
            database.locationDao().deleteContactLocationById(contactId)
        }
    }

}