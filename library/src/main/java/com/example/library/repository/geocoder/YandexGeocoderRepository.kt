package com.example.library.repository.geocoder

import com.example.java.interfaces.IYandexGeocoderRepository
import com.example.library.api.YandexGeocoderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YandexGeocoderRepository(
    private val api: YandexGeocoderApi
) : IYandexGeocoderRepository {

    override suspend fun reverseGeocoding(
        latitude: Double,
        longitude: Double,
        apikey: String
    ): String {
        val featureMemberList = withContext(Dispatchers.IO) {
            api.reverseGeocoding(
                geocode = "$longitude,$latitude",
                apikey = apikey
            ).response.geoObjectCollection.featureMember
        }
        return if (featureMemberList.isNotEmpty()) {
            featureMemberList[0].geoObject.metaDataProperty.geocoderMetaData.address.formatted
        } else {
            "Address not defined"
        }
    }
}
