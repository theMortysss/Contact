package com.example.app.di.contacts

import com.example.library.di.ContactListContainer
import dagger.Subcomponent

@Subcomponent(modules = [ContactListViewModelModule::class, ContactListModule::class])
interface ContactListComponent : ContactListContainer
