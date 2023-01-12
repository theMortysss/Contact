package com.example.library.room.entity

import androidx.room.PrimaryKey
import androidx.room.Entity
import com.example.java.entities.LocatedContact

@Entity(tableName = "marks_location_table")
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val address: String
)

fun LocatedContact.toDatabase() =
    LocationEntity(
        id = id,
        latitude = latitude,
        longitude = longitude,
        address = address
    )