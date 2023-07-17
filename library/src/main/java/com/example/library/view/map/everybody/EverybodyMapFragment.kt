package com.example.library.view.map.everybody

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
import com.example.java.entities.LocatedContact
import com.example.library.R
import com.example.library.databinding.FragmentEverybodyMapBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.TAG
import com.example.library.utils.injectViewModel
import com.example.library.viewmodel.EverybodyMapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import java.lang.Double.max
import java.lang.Double.min
import javax.inject.Inject

const val ZOOM = 12f
const val DURATION = 1f
const val AZIMUTH = 0.0f
const val TILT = 0.0f

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
    // private lateinit var placemark: PlacemarkMapObject

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
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> { mapView.map.isNightModeEnabled = true }
            Configuration.UI_MODE_NIGHT_NO -> { mapView.map.isNightModeEnabled = false }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> { mapView.map.isNightModeEnabled = false }
        }
        // curContactId = arguments?.getString(CONTACT_ID, "") ?: ""

        everybodyMapFrag?.apply {
            tv1.text = "Пользователь не выбран"
            addressTv1.text = ""
        }

        everybodyMapViewModel.getLocatedContactList()
            .observe(viewLifecycleOwner) { list ->
                if (!list.isNullOrEmpty()) {
                    locatedContactList = list
                    Log.d(TAG, "EverybodyMapFragment обзервер сработал")
                } else {
                    Toast.makeText(
                        context,
                        "Нет контактов с локацией",
                        Toast.LENGTH_SHORT
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
            mapObject as PlacemarkMapObject
            curLocatedContact = locatedContactList.first { it.id == mapObject.userData }
            showLocatedContact(curLocatedContact)
            curContactId = curLocatedContact.id
            mapView.map.move(
                CameraPosition(Point(mapObject.geometry.latitude, mapObject.geometry.longitude), ZOOM, AZIMUTH, TILT),
                Animation(Animation.Type.SMOOTH, DURATION),
                null
            )
            false
        }

    private val mapLoadedListener = MapLoadedListener {
        if (::locatedContactList.isInitialized) {
            locatedContactList.forEach {
                mapObjects.addPlacemark(
                    Point(it.latitude, it.longitude),
                    ImageProvider.fromBitmap(getBitmapFromVectorDrawable(R.drawable.baseline_place_24))
                ).apply {
                    userData = it.id
                }
            }
            val geometry = Geometry.fromBoundingBox(getBoundingBox())
            val position = mapView.map.cameraPosition(geometry, ZOOM, AZIMUTH, null)
            mapView.map.move(position, Animation(Animation.Type.LINEAR, DURATION), null)
        }
    }

    private fun getBoundingBox(): BoundingBox {
        var minLatitude = Double.MAX_VALUE
        var maxLatitude = -Double.MAX_VALUE
        var minLongitude = Double.MAX_VALUE
        var maxLongitude = -Double.MAX_VALUE

        locatedContactList.forEach {
            val annotationLat = it.latitude
            val annotationLong = it.longitude
            minLatitude = min(annotationLat, minLatitude)
            maxLatitude = max(annotationLat, maxLatitude)
            minLongitude = min(annotationLong, minLongitude)
            maxLongitude = max(annotationLong, maxLongitude)
        }
        val latitudePadding = (maxLatitude - minLatitude) / 2
        val longitudePadding = (maxLongitude - minLongitude) / 2

        return BoundingBox(
            Point(minLatitude - latitudePadding, minLongitude - longitudePadding),
            Point(maxLatitude + latitudePadding / 2, maxLongitude + longitudePadding)
        )
    }

    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null

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

    override fun onStop() {
        everybodyMapFrag!!.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        everybodyMapFrag!!.map.onStart()
        super.onStart()
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
