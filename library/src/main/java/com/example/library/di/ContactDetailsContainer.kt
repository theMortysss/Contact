package com.example.library.di

import com.example.library.view.contact.ContactDetailsFragment

interface ContactDetailsContainer {
    fun inject(contactDetailsFragment: ContactDetailsFragment)
}