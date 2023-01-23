package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class AddressDetails(
    @SerializedName("Country")
    val country: Country
)
