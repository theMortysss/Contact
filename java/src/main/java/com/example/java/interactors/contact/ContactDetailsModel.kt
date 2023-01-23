package com.example.java.interactors.contact

import com.example.java.entities.Contact
import com.example.java.interfaces.IBirthdayRepository
import com.example.java.interfaces.ICalendarRepository
import com.example.java.interfaces.IContactsRepository
import com.example.java.interfaces.ILocationRepository
import java.util.*

private const val DAY_OF_MONTH_29 = 29
private const val LEAP_YEAR_PERIOD = 4

// Для демонстрации напоминания о ДР: сдвиг вперед от текущего времени (в секундах)
private const val ALARM_SECOND_SHIFT = 20

class ContactDetailsModel(
    private val repository: IContactsRepository,
    private val locationRepository: ILocationRepository,
    private val birthdayRepository: IBirthdayRepository,
    private val calendarRepository: ICalendarRepository
) : ContactDetailsInteractor {

    override suspend fun getContactDetails(contactId: String): List<Contact> =
        repository.getContactDetails(contactId)

    override suspend fun getLocationData(contactId: String) =
        locationRepository.getLocationData(contactId)

    override suspend fun deleteLocationData(contactId: String) =
        locationRepository.deleteContactLocation(contactId)

    override fun isBirthdayAlarmOn(curContact: Contact): Boolean =
        birthdayRepository.isBirthdayAlarmOn(curContact)

    override suspend fun setBirthdayAlarm(curContact: Contact) {
        if (curContact.birthday != null) {
            birthdayRepository.setBirthdayAlarm(
                curContact,
                getAlarmStartMomentFor(curContact.birthday)
            )
        }
    }

    override suspend fun cancelBirthdayAlarm(curContact: Contact) {
        birthdayRepository.cancelBirthdayAlarm(curContact)
    }

    override fun getAlarmStartMomentFor(contactBirthday: Calendar): Calendar {
        val today = calendarRepository.now()
        val curYear = today[Calendar.YEAR]
        val alarmStartMoment = today.clone() as Calendar
        if (contactBirthday[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_29 &&
            contactBirthday[Calendar.MONTH] == Calendar.FEBRUARY
        ) { // ДР контакта = 29 февраля
            val remainder = curYear % LEAP_YEAR_PERIOD // високосные годы делятся на 4 без остатка
            if (remainder == 0) { // если текущий год високосный,
                alarmStartMoment[Calendar.DAY_OF_MONTH] = DAY_OF_MONTH_29
                alarmStartMoment[Calendar.MONTH] = Calendar.FEBRUARY
                // alarmStartMoment[Calendar.YEAR] = curYear
                // }
                // если 29 февраля в этом году, но уже прошло, перенести его на 4 года вперед
                if (alarmStartMoment.before(today)) alarmStartMoment[Calendar.YEAR] = curYear + LEAP_YEAR_PERIOD
            } else {
                // если текущий год не високосный, вычислить ближайший високосный
                alarmStartMoment[Calendar.DAY_OF_MONTH] = DAY_OF_MONTH_29
                alarmStartMoment[Calendar.MONTH] = Calendar.FEBRUARY
                alarmStartMoment[Calendar.YEAR] = curYear + (LEAP_YEAR_PERIOD - remainder)
            }
        } else { // ДР контакта - не 29 февраля
            alarmStartMoment[Calendar.DAY_OF_MONTH] = contactBirthday[Calendar.DAY_OF_MONTH]
            alarmStartMoment[Calendar.MONTH] = contactBirthday[Calendar.MONTH]
            alarmStartMoment[Calendar.YEAR] = curYear
            // Для демонстрации время срабатывания устанавливается текущее + ALARM_SECOND_SHIFT
            alarmStartMoment.add(Calendar.SECOND, ALARM_SECOND_SHIFT)
            // Если ДР в этом году, но уже прошло, перенести напоминание на следующий год
            if (alarmStartMoment.before(today)) {
                alarmStartMoment[Calendar.YEAR] = curYear + 1
            }
        }
        return alarmStartMoment
    }
}
