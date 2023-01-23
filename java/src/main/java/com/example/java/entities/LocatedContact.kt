package com.example.java.entities

data class LocatedContact(
    val id: String,
    val name: String,
    val photoUri: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String
)
