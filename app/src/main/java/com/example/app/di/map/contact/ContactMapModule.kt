package com.example.app.di.map.contact

import com.example.java.interactors.map.contact.ContactMapInteractor
import com.example.java.interactors.map.contact.ContactMapModel
import com.example.java.interfaces.IContactsRepository
import com.example.java.interfaces.ILocationRepository
import com.example.java.interfaces.IYandexGeocoderRepository
import dagger.Module
import dagger.Provides

@Module
class ContactMapModule {

    @ContactMapScope
    @Provides
    fun provideContactMapInteractor(
        locationRepository: ILocationRepository,
        contactRepository: IContactsRepository,
        geocoderRepository: IYandexGeocoderRepository
    ): ContactMapInteractor =
        ContactMapModel(
            locationRepository,
            contactRepository,
            geocoderRepository
        )
}
