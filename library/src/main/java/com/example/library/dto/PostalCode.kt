package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class PostalCode(
    @SerializedName("PostalCodeNumber")
    val postalCodeNumber: String
)
