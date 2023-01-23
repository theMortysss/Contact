package com.example.java.interactors.map.route

import com.example.java.entities.LocatedContact
import com.example.java.interfaces.ILocationRepository

class RouteMapModel(
    private val locationRepository: ILocationRepository
) : RouteMapInteractor {

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
        locationRepository.getLocatedContactList()
}
