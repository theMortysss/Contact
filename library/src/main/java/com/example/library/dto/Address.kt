package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("country_code")
    val country_code : String,
    @SerializedName("formatted")
    val formatted : String,
    @SerializedName("postal_code")
    val postal_code : String,
    @SerializedName("Components")
    val components : List<Component>
)
