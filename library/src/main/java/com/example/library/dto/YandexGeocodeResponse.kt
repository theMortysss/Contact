package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class YandexGeocodeResponse(
    @SerializedName("response")
    val response: Response
)
