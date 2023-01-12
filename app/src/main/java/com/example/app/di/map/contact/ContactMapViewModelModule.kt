package com.example.app.di.map.contact

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.ContactMapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ContactMapViewModelModule {
    @ContactMapScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactMapViewModel::class)
    abstract fun bindContactLocationsViewModel(viewModel: ContactMapViewModel): ViewModel
}