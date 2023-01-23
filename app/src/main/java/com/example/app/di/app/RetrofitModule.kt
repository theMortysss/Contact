package com.example.app.di.app

import com.example.library.api.YandexGeocoderApi
import com.example.library.utils.Constants.BASE_YANDEX_GEOCODER_API_URL
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideYandexGeocoderApi(gson: Gson): YandexGeocoderApi =
        Retrofit.Builder()
            .baseUrl(BASE_YANDEX_GEOCODER_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(YandexGeocoderApi::class.java)
}
