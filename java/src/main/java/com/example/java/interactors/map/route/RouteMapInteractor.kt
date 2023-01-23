package com.example.java.interactors.map.route

import com.example.java.entities.LocatedContact

interface RouteMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?
}
