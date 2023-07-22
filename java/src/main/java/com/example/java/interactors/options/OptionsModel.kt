package com.example.java.interactors.options

import com.example.java.interfaces.IDataStoreManager
import kotlinx.coroutines.flow.Flow

class OptionsModel(
    private val dataStore: IDataStoreManager
) : OptionsInteractor {

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.setDarkMode(enabled)
    }

    override val darkModeEnabled: Flow<Boolean>
        get() = dataStore.darkModeEnabled
}
