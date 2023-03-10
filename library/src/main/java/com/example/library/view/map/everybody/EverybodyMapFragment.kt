package com.example.library.view.map.everybody

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.java.entities.LocatedContact
import com.example.library.R
import com.example.library.databinding.FragmentEverybodyMapBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.EMPTY_VALUE
import com.example.library.utils.injectViewModel
import com.example.library.viewmodel.EverybodyMapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import javax.inject.Inject

const val IZHEVSK_LATITUDE = 56.851
const val IZHEVSK_LONGITUDE = 53.214
const val ZOOM = 12f
const val DURATION = 5f
const val AZIMUTH = 0.0f
const val TILT = 0.0f

private val TARGET_LOCATION = Point(IZHEVSK_LATITUDE, IZHEVSK_LONGITUDE)

class EverybodyMapFragment : Fragment(R.layout.fragment_everybody_map) {

    private var everybodyMapFrag: FragmentEverybodyMapBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var everybodyMapViewModel: EverybodyMapViewModel
    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var curContactId: String
    private lateinit var mapView: MapView
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var placemark: PlacemarkMapObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusEverybodyMapContainer()
            ?.inject(this)
        everybodyMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        everybodyMapFrag = FragmentEverybodyMapBinding.bind(view)
        mapView = everybodyMapFrag!!.map as MapView
        mapObjects = mapView.map.mapObjects.addCollection()
        mapView.map.setMapLoadedListener(mapLoadedListener)
        mapObjects.addTapListener(mapObjectTapListener)

        curContactId = arguments?.getString(CONTACT_ID, "") ?: ""

        everybodyMapViewModel.getLocatedContactList()
            .observe(viewLifecycleOwner) { list ->
                locatedContactList = list
                if (locatedContactList.isNotEmpty()) {
                    if (curContactId != EMPTY_VALUE) {
                        curLocatedContact = locatedContactList
                            .firstOrNull { it.id == curContactId } ?: locatedContactList[0]
                        curContactId = curLocatedContact.id
                        showLocatedContact(curLocatedContact)
                    } else {
                        curLocatedContact = locatedContactList
                            .firstOrNull { it.id == curContactId } ?: locatedContactList[0]
                        curContactId = curLocatedContact.id
                        everybodyMapFrag?.apply {
                            tv1.text = "???????????????????????? ???? ????????????"
                            addressTv1.text = ""
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "?????? ?????????????????? ?? ????????????????",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showLocatedContact(curLocatedContact: LocatedContact) {
        with(curLocatedContact) {
            everybodyMapFrag?.apply {
                tv1.text = curLocatedContact.name
                if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                    iv1.setImageURI(curLocatedContact.photoUri!!.toUri())
                }
                addressTv1.text = curLocatedContact.address
            }
        }
    }

    private val mapObjectTapListener =
        MapObjectTapListener { mapObject, point ->
            if (mapObject is PlacemarkMapObject) {
                if (mapObject != placemark) {
                    placemark = mapObject
                    curLocatedContact = locatedContactList.first { it.id == placemark.userData }
                    showLocatedContact(curLocatedContact)
                    curContactId = curLocatedContact.id
                    mapView.map.move(
                        CameraPosition(point, ZOOM, AZIMUTH, TILT),
                        Animation(Animation.Type.SMOOTH, DURATION),
                        null
                    )
                }
            }
            true
        }

    private val mapLoadedListener = MapLoadedListener {
        if (::curLocatedContact.isInitialized) {
            val curLocation = Point(curLocatedContact.latitude, curLocatedContact.longitude)
            if (::placemark.isInitialized) mapObjects.remove(placemark)
            placemark = mapObjects.addPlacemark(curLocation).apply {
                userData = curLocatedContact.id
            }

            locatedContactList.forEach {
                if (it.id != curLocatedContact.id) {
                    mapObjects.addPlacemark(Point(it.latitude, it.longitude)).apply {
                        userData = it.id
                    }
                }
            }
        }

        mapView.map.move(
            CameraPosition(TARGET_LOCATION, ZOOM, AZIMUTH, TILT),
            Animation(Animation.Type.SMOOTH, DURATION),
            null
        )
    }

    override fun onStop() {
        everybodyMapFrag!!.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        everybodyMapFrag!!.map.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        everybodyMapFrag = null
    }

    companion object {
        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = EverybodyMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
            EverybodyMapFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
