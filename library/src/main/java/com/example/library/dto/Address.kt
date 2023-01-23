package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("Components")
    val components: List<Component>,
    @SerializedName("postal_code")
    val postalCode: String,
    @SerializedName("formatted")
    val formatted: String
)
