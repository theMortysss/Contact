package com.example.app.di.map.route

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.RouteMapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class RouteMapViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RouteMapViewModel::class)
    abstract fun bindRouteMapViewModel(viewModel: RouteMapViewModel) : ViewModel
}