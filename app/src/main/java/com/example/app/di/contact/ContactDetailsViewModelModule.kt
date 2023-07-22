package com.example.app.di.contact

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.ContactDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ContactDetailsViewModelModule {

    @ContactDetailsScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactDetailsViewModel::class)
    fun bindContactDetailsViewModel(viewModel: ContactDetailsViewModel): ViewModel
}
