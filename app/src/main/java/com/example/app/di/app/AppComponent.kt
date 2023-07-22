package com.example.app.di.app

import com.example.app.di.contact.ContactDetailsComponent
import com.example.app.di.contacts.ContactListComponent
import com.example.app.di.map.contact.ContactMapComponent
import com.example.app.di.map.everybody.EverybodyMapComponent
import com.example.app.di.map.route.RouteMapComponent
import com.example.app.di.options.OptionsComponent
import com.example.app.di.viewModelFactory.ViewModelFactoryModule
import com.example.library.di.AppContainer
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class,
        ViewModelFactoryModule::class,
        RetrofitModule::class,
        LocationDatabaseModule::class,
        DataStoreModule::class
    ]
)
interface AppComponent : AppContainer {

    override fun plusContactDetailsContainer(): ContactDetailsComponent
    override fun plusContactListContainer(): ContactListComponent
    override fun plusContactMapContainer(): ContactMapComponent
    override fun plusEverybodyMapContainer(): EverybodyMapComponent
    override fun plusRouteMapContainer(): RouteMapComponent
    override fun plusOptionsContainer(): OptionsComponent
}
