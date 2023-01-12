package com.example.library.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.*

// Интеграционное тестирование
class ContactDetailsViewModelTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private lateinit var testInteractor: com.example.java.interactors.contact.ContactDetailsInteractor
    private lateinit var testViewModel: ContactDetailsViewModel

    private val iContact = com.example.java.entities.Contact(
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

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        testInteractor = mock()
        testViewModel = ContactDetailsViewModel(testInteractor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun isBirthdayAlarmOn() {
        runBlocking {
            launch(Dispatchers.Main) {
                testViewModel.isBirthdayAlarmOn(iContact)
                verify(testInteractor).isBirthdayAlarmOn(eq(iContact))
            }

        }
    }

    @Test
    fun setBirthdayAlarm() {
        runBlocking {
            launch(Dispatchers.Main) {
                testViewModel.setBirthdayAlarm(iContact).join()
                verify(testInteractor).setBirthdayAlarm(eq(iContact))
            }
        }
    }

    @Test
    fun cancelBirthdayAlarm() {
        runBlocking {
            launch(Dispatchers.Main) {
                testViewModel.cancelBirthdayAlarm(iContact).join()
                verify(testInteractor).cancelBirthdayAlarm(eq(iContact))
            }
        }
    }
}