package com.example.library.view.map.contact

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.java.entities.LocatedContact
import com.example.java.entities.LocationData
import com.example.library.R
import com.example.library.databinding.FragmentContactMapBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.TAG
import com.example.library.utils.injectViewModel
import com.example.library.viewmodel.ContactMapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import javax.inject.Inject

const val IZHEVSK_LATITUDE = 56.851
const val IZHEVSK_LONGITUDE = 53.214
const val ZOOM = 12f
const val DURATION = 1f
const val AZIMUTH = 0.0f
const val TILT = 0.0f

private val TARGET_LOCATION = Point(IZHEVSK_LATITUDE, IZHEVSK_LONGITUDE)

class ContactMapFragment : Fragment(R.layout.fragment_contact_map) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var contactMapViewModel: ContactMapViewModel
    private lateinit var contactId: String
    private lateinit var mapView: MapView
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var placemark: PlacemarkMapObject

    private var isNewContact = false
    private var curChangedLocationData: LocationData? = null
    private var contactMapFrag: FragmentContactMapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusContactMapContainer()
            ?.inject(this)
        contactMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactMapFrag = FragmentContactMapBinding.bind(view)
        mapView = contactMapFrag!!.map as MapView
        mapObjects = mapView.map.mapObjects.addCollection()
        mapView.map.setMapLoadedListener(mapLoadedListener)
        mapView.map.addInputListener(inputListener)
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> { mapView.map.isNightModeEnabled = true }
            Configuration.UI_MODE_NIGHT_NO -> { mapView.map.isNightModeEnabled = false }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> { mapView.map.isNightModeEnabled = false }
        }
        contactId = arguments?.getString(CONTACT_ID, "") ?: ""
        contactMapViewModel.getLocatedContactList().observe(viewLifecycleOwner) { locatedContactList ->
            findLocatedContact(locatedContactList)
        }
        contactMapViewModel.getShortContact(contactId).observe(viewLifecycleOwner) { shortContact ->
            if (isNewContact) {
                if (shortContact != null) {
                    curLocatedContact = LocatedContact(
                        id = shortContact.id,
                        name = shortContact.name,
                        photoUri = shortContact.photoUri,
                        latitude = 0.0,
                        longitude = 0.0,
                        address = ""
                    )
                    contactMapFrag!!.apply {
                        tv1.text = curLocatedContact.name
                        if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                            iv1.setImageURI(curLocatedContact.photoUri?.toUri())
                        }
                        addressTv1.text = curLocatedContact.address
                    }
                }
            }
        }
        contactMapViewModel.getChangedLocationData().observe(viewLifecycleOwner) { locationData ->
            if (locationData != null) {
                contactMapFrag?.saveFab?.isEnabled = false
                curChangedLocationData = locationData
                contactMapFrag?.addressTv1?.text = locationData.address
                contactMapFrag?.saveFab?.isEnabled = true
            }
        }
        contactMapFrag?.saveFab?.setOnClickListener { saveChangedLocationData() }
        contactMapFrag?.topAppBar?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveChangedLocationData() {
        val changedLocationData = curChangedLocationData
        if (changedLocationData != null) {
            curLocatedContact = curLocatedContact.copy(
                latitude = changedLocationData.latitude,
                longitude = changedLocationData.longitude,
                address = changedLocationData.address
            )
            if (isNewContact) {
                contactMapViewModel.addLocatedContact(curLocatedContact)
            } else {
                contactMapViewModel.updateLocatedContact(curLocatedContact)
            }
        }
        curChangedLocationData = null
        contactMapViewModel.resetChangedLocationData()
        Toast.makeText(context, "Локация сохранена", Toast.LENGTH_SHORT).show()
    }

    private fun findLocatedContact(locatedContactList: List<LocatedContact>) {
        val locatedContact = locatedContactList.firstOrNull { it.id == contactId }
        if (locatedContact != null) {
            curLocatedContact = locatedContact
            contactMapFrag!!.apply {
                tv1.text = curLocatedContact.name
                if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                    iv1.setImageURI(curLocatedContact.photoUri?.toUri())
                }
                addressTv1.text = curLocatedContact.address
            }
        } else {
            isNewContact = true
            contactMapViewModel.getShortContact(contactId)
        }
    }

    override fun onDestroyView() {
        contactMapFrag = null
        super.onDestroyView()
    }
    override fun onStop() {
        contactMapFrag!!.map.onStop()
        MapKitFactory.getInstance().onStop()

        super.onStop()
    }

    override fun onDestroy() {
        mapObjects.clear()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        contactMapFrag!!.map.onStart()
    }

    private val mapLoadedListener = MapLoadedListener {
        val startLocation: Point?
        if (isNewContact) {
            startLocation = TARGET_LOCATION
        } else {
            startLocation = Point(curLocatedContact.latitude, curLocatedContact.longitude)
        }

        if (::placemark.isInitialized) mapObjects.remove(placemark)
        placemark = mapObjects.addPlacemark(
            startLocation,
            ImageProvider.fromBitmap(getBitmapFromVectorDrawable(R.drawable.baseline_place_24))
        )

        mapView.map.move(
            CameraPosition(TARGET_LOCATION, ZOOM, AZIMUTH, TILT),
            Animation(Animation.Type.SMOOTH, DURATION),
            null
        )
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, point: Point) {
            Log.d(TAG, "ContactMapFragment:onMapClickListener есть клик по карте")
            if (::placemark.isInitialized) mapObjects.remove(placemark)
            placemark = mapObjects.addPlacemark(
                point,
                ImageProvider.fromBitmap(getBitmapFromVectorDrawable(R.drawable.baseline_place_24))
            )
            contactMapViewModel.setChangedLocationData(
                LocationData(
                    longitude = point.longitude,
                    latitude = point.latitude,
                    address = "..."
                )
            )
        }

        override fun onMapLongTap(p0: Map, point: Point) {
            TODO("Not yet implemented")
        }
    }
    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        var drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    companion object {
        const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = ContactMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
            ContactMapFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
