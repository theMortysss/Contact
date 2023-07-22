package com.example.app.di.options

import com.example.library.di.OptionsContainer
import dagger.Subcomponent

@OptionsScope
@Subcomponent(modules = [OptionsViewModelModule::class, OptionsModule::class])
interface OptionsComponent : OptionsContainer
