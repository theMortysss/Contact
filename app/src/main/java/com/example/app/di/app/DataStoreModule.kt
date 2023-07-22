package com.example.app.di.app

import android.content.Context
import com.example.java.interfaces.IDataStoreManager
import com.example.library.DataStoreManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(appContext: Context): IDataStoreManager =
        DataStoreManager(appContext)
}
