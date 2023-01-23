package com.example.library.api

import com.example.library.dto.YandexGeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexGeocoderApi {

    @GET("1.x/?format=json&results=1&")
    suspend fun reverseGeocoding(
        @Query("geocode") geocode: String,
        @Query("apikey") apikey: String
    ): YandexGeocodeResponse
}
