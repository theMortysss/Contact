package com.example.library.dto

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("pos")
    val pos: String
)
