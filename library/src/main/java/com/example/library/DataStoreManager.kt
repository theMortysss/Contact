package com.example.library

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.java.interfaces.IDataStoreManager
import com.example.library.DataStoreManager.PreferencesKeys.DARK_MODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

val Context.dataStore by preferencesDataStore("datastore")

class DataStoreManager @Inject constructor(
    appContext: Context
) : IDataStoreManager {
    private val dataStore = appContext.dataStore

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        withContext(Dispatchers.Default) {
            dataStore.edit { preferences ->
                preferences[DARK_MODE] = enabled
            }
        }
    }

    override val darkModeEnabled: Flow<Boolean> = runBlocking {
        withContext(Dispatchers.Default) {
            dataStore.data.map { preferences ->
                preferences[DARK_MODE] ?: false
            }
        }
    }
}
