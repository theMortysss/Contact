package com.example.app.di.map.contact

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.ContactMapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ContactMapViewModelModule {
    @ContactMapScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactMapViewModel::class)
    fun bindContactLocationsViewModel(viewModel: ContactMapViewModel): ViewModel
}
