package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class GeocoderResponseMetaData(
    @SerializedName("Point")
    val point: Point,
    @SerializedName("request")
    val request: String,
    @SerializedName("results")
    val results: String,
    @SerializedName("found")
    val found: String
)
