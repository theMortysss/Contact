package com.example.app.di.options

import com.example.java.interactors.options.OptionsInteractor
import com.example.java.interactors.options.OptionsModel
import com.example.java.interfaces.IDataStoreManager
import dagger.Module
import dagger.Provides

@Module
class OptionsModule {
    @OptionsScope
    @Provides
    fun provideOptionsInteractor(dataStore: IDataStoreManager): OptionsInteractor =
        OptionsModel(dataStore)
}
