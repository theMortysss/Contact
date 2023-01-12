package com.example.app.di.contact

import com.example.library.di.ContactDetailsContainer
import dagger.Subcomponent

@Subcomponent(modules = [ContactDetailsViewModelModule::class, ContactDetailsModule::class])
interface ContactDetailsComponent : ContactDetailsContainer