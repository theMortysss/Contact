package com.example.app.di.viewModelFactory

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass
import dagger.MapKey

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
