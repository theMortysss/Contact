package com.example.app.di.options

import androidx.lifecycle.ViewModel
import com.example.app.di.viewModelFactory.ViewModelKey
import com.example.library.viewmodel.OptionsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface OptionsViewModelModule {

    @OptionsScope
    @Binds
    @IntoMap
    @ViewModelKey(OptionsViewModel::class)
    fun bindOptionsViewModel(viewModel: OptionsViewModel): ViewModel
}
