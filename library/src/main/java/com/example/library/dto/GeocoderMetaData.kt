package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class GeocoderMetaData(
    @SerializedName("precision")
    val precision: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("Address")
    val address: Address,
    @SerializedName("AddressDetails")
    val addressDetails: AddressDetails
)
