package com.example.library.dto

import com.example.library.dto.GeoObjectCollection
import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("GeoObjectCollection")
    val geoObjectCollection : GeoObjectCollection
)
