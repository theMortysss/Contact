package com.example.app.di.map.everybody

import com.example.library.di.EverybodyMapContainer
import dagger.Subcomponent
@EverybodyMapScope
@Subcomponent(modules = [EverybodyMapViewModelModule::class, EverybodyMapModule::class])
interface EverybodyMapComponent : EverybodyMapContainer
