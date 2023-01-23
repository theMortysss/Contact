package com.example.app.di.app

import android.content.Context
import androidx.room.Room
import com.example.library.room.database.LocationDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationDatabaseModule {

    @Singleton
    @Provides
    fun provideLocationDatabase(context: Context): LocationDatabase =
        Room.databaseBuilder(
            context,
            LocationDatabase::class.java,
            "LocationDatabase"
        ).build()
}
