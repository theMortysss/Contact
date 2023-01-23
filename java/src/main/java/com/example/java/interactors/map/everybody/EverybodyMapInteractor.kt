package com.example.java.interactors.map.everybody

import com.example.java.entities.LocatedContact

interface EverybodyMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?
}
