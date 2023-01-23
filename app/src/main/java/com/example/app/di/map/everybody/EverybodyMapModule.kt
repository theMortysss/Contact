package com.example.app.di.map.everybody

import com.example.java.interactors.map.everybody.EverybodyMapInteractor
import com.example.java.interactors.map.everybody.EverybodyMapModel
import com.example.java.interfaces.ILocationRepository
import dagger.Module
import dagger.Provides

@Module
class EverybodyMapModule {

    @Provides
    fun provideEverybodyMapInteractor(
        locationRepository: ILocationRepository
    ): EverybodyMapInteractor = EverybodyMapModel(locationRepository)
}
