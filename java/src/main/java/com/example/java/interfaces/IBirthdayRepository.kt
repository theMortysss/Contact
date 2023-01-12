package com.example.java.interfaces

import com.example.java.entities.Contact
import java.util.*

interface IBirthdayRepository {
    fun isBirthdayAlarmOn(curContact: Contact): Boolean

    fun setBirthdayAlarm(curContact: Contact, alarmStartMoment: Calendar)

    fun cancelBirthdayAlarm(curContact: Contact)
}