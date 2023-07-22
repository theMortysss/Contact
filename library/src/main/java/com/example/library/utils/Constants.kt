package com.example.library.utils

import android.net.Uri
import com.example.library.BuildConfig

object Constants {
    const val MAPKIT_API_KEY = BuildConfig.MAPKIT_API_KEY
    const val GEOCODER_API_KEY = BuildConfig.GEOCODER_API_KEY

    const val TAG = "Contacts"
    const val CONTACT_ID = "id"
    const val BIRTHDAY_MESSAGE = "b-msg"
    const val CHANNEL_ID = "com.example.contacts" // id канала нотификаций
    const val ANDROID_RESOURCE = "android.resource://"
    const val BASE_YANDEX_GEOCODER_API_URL = "https://geocode-maps.yandex.ru/" // для Retrofit
    const val EMPTY_VALUE = ""
    lateinit var notificationSound: Uri // звук нотификации
}
