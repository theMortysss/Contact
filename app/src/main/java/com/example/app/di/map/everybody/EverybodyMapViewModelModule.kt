package com.example.app.di.map.everybody

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.EverybodyMapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface EverybodyMapViewModelModule {

    @EverybodyMapScope
    @Binds
    @IntoMap
    @ViewModelKey(EverybodyMapViewModel::class)
    fun bindEverybodyMapViewModel(viewModel: EverybodyMapViewModel): ViewModel
}
