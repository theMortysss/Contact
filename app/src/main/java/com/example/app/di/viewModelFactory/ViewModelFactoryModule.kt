package com.example.app.di.viewModelFactory

import androidx.lifecycle.ViewModelProvider
import com.example.library.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
