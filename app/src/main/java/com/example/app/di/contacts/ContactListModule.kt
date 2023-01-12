package com.example.app.di.contacts

import com.example.java.interfaces.IContactsRepository
import dagger.Module
import dagger.Provides

@Module
class ContactListModule {
    @Provides
    fun provideContactListInteractor(repository: IContactsRepository) : com.example.java.interactors.contacts.ContactListInteractor
        = com.example.java.interactors.contacts.ContactListModel(repository)
}