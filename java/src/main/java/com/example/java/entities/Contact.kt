package com.example.java.entities

import java.util.*

data class Contact(
    val id: String,
    val name: String,
    val birthday: Calendar?,
    val phone: String?,
    val email: String?,
    val description: String?,
    val avatarUri: String?
)