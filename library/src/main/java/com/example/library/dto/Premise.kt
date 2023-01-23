package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class Premise(
    @SerializedName("PremiseNumber")
    val premiseNumber: String,
    @SerializedName("PostalCode")
    val postalCode: PostalCode
)
