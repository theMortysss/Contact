package com.example.library.di

import com.example.library.view.map.contact.ContactMapFragment

interface ContactMapContainer {

    fun inject(contactMapFragment: ContactMapFragment)
}