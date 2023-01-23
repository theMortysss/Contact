package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class MetaDataPropertyGO(
    @SerializedName("GeocoderMetaData")
    val geocoderMetaData: GeocoderMetaData
)
