package com.example.app.di.contacts

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.ContactListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ContactListViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    fun bindContactListViewModel(viewModel: ContactListViewModel): ViewModel
}
