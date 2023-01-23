package com.example.java.interactors.contact

import com.example.java.entities.Contact
import com.example.java.entities.LocationData
import java.util.*

interface ContactDetailsInteractor {
    suspend fun getContactDetails(contactId: String): List<Contact>

    suspend fun getLocationData(contactId: String): LocationData?

    suspend fun deleteLocationData(contactId: String)

    suspend fun setBirthdayAlarm(curContact: Contact)

    suspend fun cancelBirthdayAlarm(curContact: Contact)

    fun isBirthdayAlarmOn(curContact: Contact): Boolean

    fun getAlarmStartMomentFor(contactBirthday: Calendar): Calendar
}
