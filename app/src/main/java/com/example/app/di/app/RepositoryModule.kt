package com.example.app.di.app

import android.content.ContentResolver
import android.content.Context
import com.example.java.interfaces.IBirthdayRepository
import com.example.java.interfaces.ICalendarRepository
import com.example.java.interfaces.IContactsRepository
import com.example.java.interfaces.ILocationRepository
import com.example.java.interfaces.IYandexGeocoderRepository
import com.example.library.api.YandexGeocoderApi
import com.example.library.repository.alarm.BirthdayRepository
import com.example.library.repository.alarm.CalendarRepository
import com.example.library.repository.contacts.ContactsRepository
import com.example.library.repository.geocoder.YandexGeocoderRepository
import com.example.library.repository.location.LocationRepository
import com.example.library.room.database.LocationDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideContactsRepository(contentResolver: ContentResolver): IContactsRepository =
        ContactsRepository(contentResolver)

    @Singleton
    @Provides
    fun provideLocationRepository(
        database: LocationDatabase,
        contactsRepositoryInterface: IContactsRepository
    ): ILocationRepository =
        LocationRepository(database, contactsRepositoryInterface)

    @Singleton
    @Provides
    fun provideYandexGeocoderRepository(
        api: YandexGeocoderApi
    ): IYandexGeocoderRepository = YandexGeocoderRepository(api)

    @Singleton
    @Provides
    fun provideBirthdayRepository(
        appContext: Context
    ): IBirthdayRepository = BirthdayRepository(appContext)

    @Singleton
    @Provides
    fun provideCalendarRepository(): ICalendarRepository = CalendarRepository()
}
