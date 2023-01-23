package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class GeoObject(
    @SerializedName("metaDataProperty")
    val metaDataProperty: MetaDataPropertyGO,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("boundedBy")
    val boundedBy: BoundedBy,
    @SerializedName("Point")
    val point: PointGO
)
