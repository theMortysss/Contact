package com.example.java.interfaces

interface IYandexGeocoderRepository {

    suspend fun reverseGeocoding(
        latitude: Double,
        longitude: Double,
        apikey: String
    ): String
}
