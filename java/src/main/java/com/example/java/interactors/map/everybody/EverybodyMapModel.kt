package com.example.java.interactors.map.everybody

import com.example.java.entities.LocatedContact
import com.example.java.interfaces.ILocationRepository

class EverybodyMapModel(private val locationRepository: ILocationRepository) :
    EverybodyMapInteractor {

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
        locationRepository.getLocatedContactList()

}