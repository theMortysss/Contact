package com.example.java.interactors.options

import kotlinx.coroutines.flow.Flow
import java.util.*

interface OptionsInteractor {

    suspend fun setDarkMode(enabled: Boolean)

    val darkModeEnabled: Flow<Boolean>
}
