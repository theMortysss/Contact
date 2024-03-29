package com.example.library.view.map.everybody

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.java.entities.LocatedContact
import com.example.library.R
import com.example.library.databinding.FragmentEverybodyMapBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.TAG
import com.example.library.utils.DrawbleToBitmap.getBitmapFromVectorDrawable
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

private const val ZOOM = 12f
private const val DURATION = 1f
private const val AZIMUTH = 0.0f
private const val TILT = 0.0f

class EverybodyMapFragment : Fragment(R.layout.fragment_everybody_map) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var everybodyMapViewModel: EverybodyMapViewModel

    private var _everybodyMapFrag: FragmentEverybodyMapBinding? = null
    private val everybodyMapFrag: FragmentEverybodyMapBinding get() = _everybodyMapFrag!!

    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var curContactId: String
    private lateinit var mapView: MapView
    private lateinit var mapObjects: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusEverybodyMapContainer()
            ?.inject(this)
        everybodyMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _everybodyMapFrag = FragmentEverybodyMapBinding.inflate(inflater, container, false)
        return everybodyMapFrag.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = everybodyMapFrag.map as MapView
        mapObjects = mapView.map.mapObjects.addCollection()
        mapView.map.setMapLoadedListener(mapLoadedListener)
        mapObjects.addTapListener(mapObjectTapListener)

        darkModeImpl()

        getContactList()
    }

    private fun getContactList() {
        everybodyMapFrag.apply {
            tv1.text = getString(R.string.noContactSelected)
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
                        getString(R.string.locatedContactsNotFound),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun darkModeImpl() {
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> { mapView.map.isNightModeEnabled = true }
            Configuration.UI_MODE_NIGHT_NO -> { mapView.map.isNightModeEnabled = false }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> { mapView.map.isNightModeEnabled = false }
        }
    }

    private fun showLocatedContact(curLocatedContact: LocatedContact) {
        everybodyMapFrag.apply {
            tv1.text = curLocatedContact.name
            if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                iv1.setImageURI(curLocatedContact.photoUri!!.toUri())
            }
            addressTv1.text = curLocatedContact.address
        }
    }

    private val mapObjectTapListener =
        MapObjectTapListener { mapObject, _ ->
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
                    ImageProvider.fromBitmap(
                        getBitmapFromVectorDrawable(requireContext(), R.drawable.baseline_place_24)
                    )
                ).apply {
                    userData = it.id
                }
            }
            val geometry = Geometry.fromBoundingBox(getBoundingBox())
            val position = mapView.map.cameraPosition(geometry, null, null, null)
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

    override fun onStop() {
        everybodyMapFrag.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        everybodyMapFrag.map.onStart()
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _everybodyMapFrag = null
    }

    companion object {
        private const val CONTACT_ID = "id"

        @JvmStatic
        fun newInstance(contactId: String) =
            EverybodyMapFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
