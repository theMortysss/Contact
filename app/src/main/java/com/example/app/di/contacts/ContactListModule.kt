package com.example.app.di.contacts

import com.example.java.interactors.contacts.ContactListInteractor
import com.example.java.interactors.contacts.ContactListModel
import com.example.java.interfaces.IContactsRepository
import dagger.Module
import dagger.Provides

@Module
class ContactListModule {
    @Provides
    fun provideContactListInteractor(
        repository: IContactsRepository
    ): ContactListInteractor = ContactListModel(repository)
}
