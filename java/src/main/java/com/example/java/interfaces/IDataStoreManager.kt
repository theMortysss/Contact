package com.example.java.interfaces

import kotlinx.coroutines.flow.Flow

interface IDataStoreManager {

    suspend fun setDarkMode(enabled: Boolean)

    val darkModeEnabled: Flow<Boolean>
}
