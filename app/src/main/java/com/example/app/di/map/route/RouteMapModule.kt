package com.example.app.di.map.route

import com.example.java.interactors.map.route.RouteMapInteractor
import com.example.java.interactors.map.route.RouteMapModel
import com.example.java.interfaces.ILocationRepository
import dagger.Module
import dagger.Provides

@Module
class RouteMapModule {

    @Provides
    fun provideRouteMapInteractor(
        locationRepository: ILocationRepository
    ): RouteMapInteractor = RouteMapModel(locationRepository)
}
