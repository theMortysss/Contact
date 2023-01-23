package com.example.library.di

import com.example.library.view.contacts.ContactListFragment

interface ContactListContainer {

    fun inject(contactListFragment: ContactListFragment)
}
