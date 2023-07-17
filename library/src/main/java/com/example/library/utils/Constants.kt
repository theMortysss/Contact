package com.example.library.utils

import android.net.Uri

object Constants {

    const val MAPKIT_API_KEY = "37637e07-18dc-48f0-b4d1-7443eb500e8d"
    const val GEOCODER_API_KEY = "9f6aa7f9-21a1-4ea1-b18a-b836a3c878f3"
    const val TAG = "Contacts"
    const val CONTACT_ID = "id"
    const val BIRTHDAY_MESSAGE = "b-msg"
    const val CHANNEL_ID = "com.example.contacts" // id канала нотификаций
    const val ANDROID_RESOURCE = "android.resource://"
    const val BASE_YANDEX_GEOCODER_API_URL = "https://geocode-maps.yandex.ru/" // для Retrofit
    const val EMPTY_VALUE = ""
    lateinit var notificationSound: Uri // звук нотификации
}
