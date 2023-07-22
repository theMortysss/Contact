package com.example.app.di.contact

import com.example.java.interactors.contact.ContactDetailsInteractor
import com.example.java.interactors.contact.ContactDetailsModel
import com.example.java.interfaces.IBirthdayRepository
import com.example.java.interfaces.ICalendarRepository
import com.example.java.interfaces.IContactsRepository
import com.example.java.interfaces.ILocationRepository
import dagger.Module
import dagger.Provides

@Module
class ContactDetailsModule {

    @ContactDetailsScope
    @Provides
    fun provideContactDetailsInteractor(
        contactsRepository: IContactsRepository,
        locationRepository: ILocationRepository,
        birthdayRepository: IBirthdayRepository,
        calendarRepository: ICalendarRepository
    ): ContactDetailsInteractor =
        ContactDetailsModel(
            contactsRepository,
            locationRepository,
            birthdayRepository,
            calendarRepository
        )
}
