package com.example.app.di.map.contact

import com.example.library.di.ContactMapContainer
import dagger.Subcomponent

@ContactMapScope
@Subcomponent(modules = [ContactMapViewModelModule::class, ContactMapModule::class])
interface ContactMapComponent : ContactMapContainer
