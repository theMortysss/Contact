package com.example.library.view.map.route

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.java.entities.LocatedContact
import com.example.library.R
import com.example.library.databinding.FragmentRouteMapBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.injectViewModel
import com.example.library.view.map.ContactDialogFragment
import com.example.library.view.map.DIALOG_REQUEST
import com.example.library.viewmodel.RouteMapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.SectionMetadata
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import javax.inject.Inject

const val SELECTED_CONTACT_ID = "selected_id"
const val IZHEVSK_LATITUDE = 56.851
const val IZHEVSK_LONGITUDE = 53.214
const val ZOOM = 12f
const val DURATION = 5f
const val AZIMUTH = 0.0f
const val TILT = 0.0f

private val TARGET_LOCATION = Point(IZHEVSK_LATITUDE, IZHEVSK_LONGITUDE)

class RouteMapFragment : Fragment(R.layout.fragment_route_map) {

    private var routeMapFrag: FragmentRouteMapBinding? = null
    private var pdRouter: PedestrianRouter? = null
    private val points: MutableList<RequestPoint> = ArrayList()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var routeMapViewModel: RouteMapViewModel
    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var curContactId: String
    private lateinit var mapView: MapView
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var placemark: PlacemarkMapObject
    private lateinit var dialogFragment: ContactDialogFragment
    private lateinit var secondLocatedContact: LocatedContact
    private lateinit var polylineMapObject: PolylineMapObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusRouteMapContainer()
            ?.inject(this)
        routeMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routeMapFrag = FragmentRouteMapBinding.bind(view)
        mapView = routeMapFrag!!.map as MapView
        mapObjects = mapView.map.mapObjects.addCollection()
        mapView.map.setMapLoadedListener(mapLoadedListener)
        curContactId = arguments?.getString(CONTACT_ID, "") ?: ""
        routeMapViewModel.getLocatedContactList()
            .observe(viewLifecycleOwner) { list ->
                locatedContactList = list
                if (locatedContactList.isNotEmpty()) {
                    curLocatedContact = locatedContactList
                        .firstOrNull { it.id == curContactId } ?: locatedContactList[0]
                    // curContactId = curLocatedContact.id
                    showFirstContact(curLocatedContact)
                    routeMapFrag?.apply {
                        tv2.text = "Пользователь не выбран"
                        addressTv2.text = ""
                    }
                    dialogFragment = ContactDialogFragment
                        .newInstance(locatedContactList.map { it.name } as ArrayList<String>)
                    setFragmentResultListener(DIALOG_REQUEST, contactListener)
                } else {
                    Toast.makeText(
                        context,
                        "Нет контактов с локацией",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private val contactListener: (String, Bundle) -> Unit = { _, bundle ->
        if (points.isNotEmpty()) points.clear()
        val id = bundle.getInt(SELECTED_CONTACT_ID)
        secondLocatedContact = locatedContactList[id]
        showSecondContact(secondLocatedContact)

        points.add(
            RequestPoint(
                Point(curLocatedContact.latitude, curLocatedContact.longitude),
                RequestPointType.WAYPOINT,
                null
            )
        )
        points.add(
            RequestPoint(
                Point(secondLocatedContact.latitude, secondLocatedContact.longitude),
                RequestPointType.WAYPOINT,
                null
            )
        )

        pdRouter = TransportFactory.getInstance().createPedestrianRouter()
        pdRouter!!.requestRoutes(points, TimeOptions(), routeListener)
    }

    private fun showSecondContact(secondLocatedContact: LocatedContact) {
        with(secondLocatedContact) {
            routeMapFrag?.apply {
                tv2.text = secondLocatedContact.name
                if (!secondLocatedContact.photoUri.isNullOrEmpty()) {
                    iv2.setImageURI(secondLocatedContact.photoUri!!.toUri())
                }
                addressTv2.text = secondLocatedContact.address
            }
        }
    }

    private fun showFirstContact(curLocatedContact: LocatedContact) {
        with(curLocatedContact) {
            routeMapFrag?.apply {
                tv1.text = curLocatedContact.name
                if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                    iv1.setImageURI(curLocatedContact.photoUri!!.toUri())
                }
                addressTv1.text = curLocatedContact.address
            }
        }
    }

    private val routeListener = object : Session.RouteListener {
        override fun onMasstransitRoutes(routes: MutableList<Route>) {
            if (routes.isNotEmpty()) {
                for (section in routes[0].sections) {
                    drawSection(
                        section.metadata.data,
                        SubpolylineHelper.subpolyline(
                            routes[0].geometry,
                            section.geometry
                        )
                    )
                }
            }
        }

        override fun onMasstransitRoutesError(error: Error) {
            var errorMessage = "unknown_error_message"
            if (error is RemoteError) {
                errorMessage = "remote_error_message"
            } else if (error is NetworkError) {
                errorMessage = "network_error_message"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        private fun drawSection(
            data: SectionMetadata.SectionData,
            geometry: Polyline
        ) {
            if (::polylineMapObject.isInitialized) {
                mapObjects.remove(polylineMapObject)
            }
            polylineMapObject = mapObjects.addPolyline(geometry)

            if (data.transports == null) {
                polylineMapObject.setStrokeColor(-0x10000)
            }
        }
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
        routeMapFrag?.addSecondContactFab?.setOnClickListener {
            dialogFragment.show(parentFragmentManager, "dialog")
        }
    }

    override fun onStop() {
        routeMapFrag!!.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        routeMapFrag!!.map.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        routeMapFrag = null
    }

    companion object {
        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = RouteMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
            RouteMapFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
