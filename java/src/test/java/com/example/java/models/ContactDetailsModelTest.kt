package com.example.java.models

import com.example.java.entities.Contact
import com.example.java.interactors.contact.ContactDetailsModel
import com.example.java.interfaces.IBirthdayRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.*

// Модульное тестирование
class ContactDetailsModelTest {

    private val birthdayRepository: IBirthdayRepository = mock()

    private val iContact = Contact(
        id = "1",
        name = "Иван Иванович",
        email = "",
        description = "",
        phone = "",
        birthday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 8)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.YEAR, 1990)
        },
        avatarUri = ""
    )
    private val pContact = Contact(
        id = "2",
        name = "Павел Павлович",
        email = "",
        description = "",
        phone = "",
        birthday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 29)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.YEAR, 1980)
        },
        avatarUri = ""
    )

//    Сценарий: успешное добавление напоминания
//    Текущий год - 1999(не високосный) 9 сентября
//    Есть контакт Иван Иванович с датой рождения 8 сентября
//    И напоминание для этого контакта отсутствует
//
//    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович
//
//    Тогда происходит успешное добавление напоминания на 2000 год 8 сентября
    @Test
    fun test1() {
        val testModel = ContactDetailsModel(
            birthdayRepository = birthdayRepository,
            locationRepository = mock(),
            repository = mock(),
            calendarRepository = mock() {
                on { now() }.then {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 9)
                        set(Calendar.MONTH, Calendar.SEPTEMBER)
                        set(Calendar.YEAR, 1999)
                    }
                }
            }
        )
        runBlocking {
            testModel.setBirthdayAlarm(iContact)
        }
        verify(birthdayRepository).setBirthdayAlarm(
            curContact = eq(iContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == 8 &&
                    it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                    it[Calendar.YEAR] == 2000
            }
        )
    }

//    Сценарий: успешное добавление напоминания, ДР еще в текущем году не было
//    Текущий год - 1999(не високосный) 7 сентября
//    Есть контакт Иван Иванович с датой рождения 8 сентября
//    И напоминание для этого контакта отсутствует
//
//    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович
//
//    Тогда происходит успешное добавление напоминания на 1999 год 8 сентября
    @Test
    fun test2() {
        val testModel = ContactDetailsModel(
            birthdayRepository = birthdayRepository,
            locationRepository = mock(),
            repository = mock(),
            calendarRepository = mock() {
                on { now() }.then {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 7)
                        set(Calendar.MONTH, Calendar.SEPTEMBER)
                        set(Calendar.YEAR, 1999)
                    }
                }
            }
        )
        runBlocking {
            testModel.setBirthdayAlarm(iContact)
        }
        verify(birthdayRepository).setBirthdayAlarm(
            curContact = eq(iContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == 8 &&
                    it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                    it[Calendar.YEAR] == 1999
            }
        )
    }

//    Сценарий: успешное удаление напоминания
//    Текущий год - 1999(не високосный)
//    Есть контакт Иван Иванович с датой рождения 8 сентября
//    И для него включено напоминание на 2000 год 8 сентября
//
//    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович
//
//    Тогда происходит успешное удаление напоминания
    @Test
    fun test3() {
        val testModel = ContactDetailsModel(
            birthdayRepository = birthdayRepository,
            locationRepository = mock(),
            repository = mock(),
            calendarRepository = mock() {
                on { now() }.then {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 7)
                        set(Calendar.MONTH, Calendar.SEPTEMBER)
                        set(Calendar.YEAR, 1999)
                    }
                }
            }
        )
        runBlocking {
            testModel.setBirthdayAlarm(iContact)
            testModel.cancelBirthdayAlarm(iContact)
        }
        verify(birthdayRepository).cancelBirthdayAlarm(eq(iContact))
    }

//    Сценарий: добавление напоминания для контакта родившегося 29 февраля
//    Текущий год - 1999(не високосный), следующий 2000(високосный) 2 марта
//    Есть контакт Павел Павлович с датой рождения 29 февраля
//    И напоминание для этого контакта отсутствует
//
//    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Павел Павлович
//
//    Тогда происходит успешное добавление напоминания на 2000 год 29 февраля
    @Test
    fun test4() {
        val testModel = ContactDetailsModel(
            birthdayRepository = birthdayRepository,
            locationRepository = mock(),
            repository = mock(),
            calendarRepository = mock() {
                on { now() }.then {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 2)
                        set(Calendar.MONTH, Calendar.MARCH)
                        set(Calendar.YEAR, 1999)
                    }
                }
            }
        )
        runBlocking {
            testModel.setBirthdayAlarm(pContact)
        }
        verify(birthdayRepository).setBirthdayAlarm(
            curContact = eq(pContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == 29 &&
                    it[Calendar.MONTH] == Calendar.FEBRUARY &&
                    it[Calendar.YEAR] == 2000
            }
        )
    }

//    Сценарий: добавление напоминания для контакта родившегося 29 февраля в високосный год
//    Текущий год - 2000(високосный) 1 марта
//    Есть контакт Павел Павлович с датой рождения 29 февраля
//    И напоминание для этого контакта отсутствует
//
//    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Павел Павлович
//
//    Тогда происходит успешное добавление напоминания на 2004 год 29 февраля
//    (т.е. пропускаем 4 года, представим, что бедняги празднуют ДР раз в 4 года)
    @Test
    fun test5() {
        val testModel = ContactDetailsModel(
            birthdayRepository = birthdayRepository,
            locationRepository = mock(),
            repository = mock(),
            calendarRepository = mock() {
                on { now() }.then {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.MONTH, Calendar.MARCH)
                        set(Calendar.YEAR, 2000)
                    }
                }
            }
        )
        runBlocking {
            testModel.setBirthdayAlarm(pContact)
        }
        verify(birthdayRepository).setBirthdayAlarm(
            curContact = eq(pContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == 29 &&
                    it[Calendar.MONTH] == Calendar.FEBRUARY &&
                    it[Calendar.YEAR] == 2004
            }
        )
    }
}
