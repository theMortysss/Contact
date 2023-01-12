package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class GeoObjectCollection(
    @SerializedName("metaDataProperty")
    val metaDataProperty : MetaDataProperty,
    @SerializedName("featureMember")
    val featureMember : List<FeatureMember>
)
