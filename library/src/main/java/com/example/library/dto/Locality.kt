package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class Locality(
    @SerializedName("LocalityName")
    val localityName: String,
    @SerializedName("Thoroughfare")
    val thoroughfare: Thoroughfare
)
