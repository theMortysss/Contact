package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class MetaDataProperty(
    @SerializedName("GeocoderResponseMetaData")
    val geocoderResponseMetaData: GeocoderResponseMetaData
)
