package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class SubAdministrativeArea(
    @SerializedName("SubAdministrativeAreaName")
    val subAdministrativeAreaName: String,
    @SerializedName("Locality")
    val locality: Locality
)
