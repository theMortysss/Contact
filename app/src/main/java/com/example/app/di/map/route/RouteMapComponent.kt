package com.example.app.di.map.route

import com.example.library.di.RouteMapContainer
import dagger.Subcomponent
@RouteMapScope
@Subcomponent(modules = [RouteMapViewModelModule::class, RouteMapModule::class])
interface RouteMapComponent : RouteMapContainer
