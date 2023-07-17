package com.example.library.view.map.route

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.yandex.mapkit.geometry.*
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import javax.inject.Inject

const val SELECTED_CONTACT_ID = "selected_id"
const val ZOOM = 12f
const val DURATION = 1f
const val AZIMUTH = 0.0f
const val TILT = 0.0f

class RouteMapFragment : Fragment(R.layout.fragment_route_map) {

    private var routeMapFrag: FragmentRouteMapBinding? = null
    private var pdRouter: PedestrianRouter? = null
    private val points: MutableList<RequestPoint> = ArrayList()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var routeMapViewModel: RouteMapViewModel
    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var firstLocatedContact: LocatedContact
    private lateinit var mapView: MapView
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var dialogFragment1: ContactDialogFragment
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
        mapView = routeMapFrag!!.map
        mapObjects = mapView.map.mapObjects.addCollection()
        mapView.map.setMapLoadedListener(mapLoadedListener)

        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> { mapView.map.isNightModeEnabled = true }
            Configuration.UI_MODE_NIGHT_NO -> { mapView.map.isNightModeEnabled = false }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> { mapView.map.isNightModeEnabled = false }
        }

        routeMapViewModel.getLocatedContactList()
            .observe(viewLifecycleOwner) { list ->
                locatedContactList = list
                if (locatedContactList.isNotEmpty()) {
                    routeMapFrag?.apply {
                        tv1.text = "Пользователь не выбран"
                        addressTv1.text = ""
                        tv2.text = "Пользователь не выбран"
                        addressTv2.text = ""
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Нет контактов с локацией",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    private val contactListener1: (String, Bundle) -> Unit = { _, bundle ->
        if (points.isNotEmpty()) points.clear()
        val id = bundle.getInt(SELECTED_CONTACT_ID)
        firstLocatedContact = locatedContactList[id]
        showFirstContact(firstLocatedContact)
        showPoints()
    }

    private val contactListener2: (String, Bundle) -> Unit = { _, bundle ->
        if (points.isNotEmpty()) points.clear()
        val id = bundle.getInt(SELECTED_CONTACT_ID)
        secondLocatedContact = locatedContactList[id]
        showSecondContact(secondLocatedContact)
        showPoints()
    }

    private fun showPoints() {
        if (::firstLocatedContact.isInitialized and ::secondLocatedContact.isInitialized) {
            points.add(
                RequestPoint(
                    Point(firstLocatedContact.latitude, firstLocatedContact.longitude),
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
    }

    private fun showSecondContact(secondLocatedContact: LocatedContact) {
        routeMapFrag?.apply {
            tv2.text = secondLocatedContact.name
            if (!secondLocatedContact.photoUri.isNullOrEmpty()) {
                iv2.setImageURI(secondLocatedContact.photoUri!!.toUri())
            }
            addressTv2.text = secondLocatedContact.address
        }
        mapObjects.addPlacemark(
            Point(secondLocatedContact.latitude, secondLocatedContact.longitude),
            ImageProvider.fromBitmap(getBitmapFromVectorDrawable(R.drawable.baseline_place_24))
        ).apply {
            userData = secondLocatedContact.id
        }
    }

    private fun showFirstContact(firstLocatedContact: LocatedContact) {
        routeMapFrag?.apply {
            tv1.text = firstLocatedContact.name
            if (!firstLocatedContact.photoUri.isNullOrEmpty()) {
                iv1.setImageURI(firstLocatedContact.photoUri!!.toUri())
            }
            addressTv1.text = firstLocatedContact.address
        }
        mapObjects.addPlacemark(
            Point(firstLocatedContact.latitude, firstLocatedContact.longitude),
            ImageProvider.fromBitmap(getBitmapFromVectorDrawable(R.drawable.baseline_place_24))
        ).apply {
            userData = firstLocatedContact.id
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
                val geometry = Geometry.fromBoundingBox(getBoundingBox(routes[0].geometry.points))
                val position = mapView.map.cameraPosition(geometry, ZOOM, AZIMUTH, null)
                mapView.map.move(position, Animation(Animation.Type.LINEAR, DURATION), null)
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
                polylineMapObject.setStrokeColor(resources.getColor(android.R.color.holo_green_light))
            }
        }
    }

    private val mapLoadedListener = MapLoadedListener {
        // < Временное
        if (::locatedContactList.isInitialized) {
            locatedContactList.forEach {
                mapObjects.addPlacemark(
                    Point(it.latitude, it.longitude),
                    ImageProvider.fromBitmap(getBitmapFromVectorDrawable(R.drawable.baseline_place_24))
                ).apply {
                    userData = it.id
                }
            }
            val geometry = Geometry.fromBoundingBox(getBoundingBoxX(locatedContactList))
            val position = mapView.map.cameraPosition(geometry, ZOOM, AZIMUTH, null)
            mapView.map.move(position, Animation(Animation.Type.LINEAR, DURATION), null)
        }
        // Временное >
        routeMapFrag?.firstContactCard?.setOnClickListener {
            dialogFragment1 = ContactDialogFragment
                .newInstance(locatedContactList.map { it.name } as ArrayList<String>)
            setFragmentResultListener(
                DIALOG_REQUEST,
                contactListener1
            )
            dialogFragment1.show(parentFragmentManager, "dialog1")
        }
        routeMapFrag?.secondContactCard?.setOnClickListener {
            dialogFragment1 = ContactDialogFragment
                .newInstance(locatedContactList.map { it.name } as ArrayList<String>)
            setFragmentResultListener(
                DIALOG_REQUEST,
                contactListener2
            )
            dialogFragment1.show(parentFragmentManager, "dialog2")
        }
    }

    // < Временное
    private fun getBoundingBox(points: MutableList<Point>): BoundingBox {
        var minLatitude = Double.MAX_VALUE
        var maxLatitude = -Double.MAX_VALUE
        var minLongitude = Double.MAX_VALUE
        var maxLongitude = -Double.MAX_VALUE

        points.forEach {
            val annotationLat = it.latitude
            val annotationLong = it.longitude
            minLatitude = java.lang.Double.min(annotationLat, minLatitude)
            maxLatitude = java.lang.Double.max(annotationLat, maxLatitude)
            minLongitude = java.lang.Double.min(annotationLong, minLongitude)
            maxLongitude = java.lang.Double.max(annotationLong, maxLongitude)
        }
        val latitudePadding = (maxLatitude - minLatitude) / 2
        val longitudePadding = (maxLongitude - minLongitude) / 2

        return BoundingBox(
            Point(minLatitude - latitudePadding, minLongitude - longitudePadding),
            Point(maxLatitude + latitudePadding / 2, maxLongitude + longitudePadding)
        )
    }

    // Временное
    private fun getBoundingBoxX(points: List<LocatedContact>): BoundingBox {
        var minLatitude = Double.MAX_VALUE
        var maxLatitude = -Double.MAX_VALUE
        var minLongitude = Double.MAX_VALUE
        var maxLongitude = -Double.MAX_VALUE

        points.forEach {
            val annotationLat = it.latitude
            val annotationLong = it.longitude
            minLatitude = java.lang.Double.min(annotationLat, minLatitude)
            maxLatitude = java.lang.Double.max(annotationLat, maxLatitude)
            minLongitude = java.lang.Double.min(annotationLong, minLongitude)
            maxLongitude = java.lang.Double.max(annotationLong, maxLongitude)
        }
        val latitudePadding = (maxLatitude - minLatitude) / 2
        val longitudePadding = (maxLongitude - minLongitude) / 2

        return BoundingBox(
            Point(minLatitude - latitudePadding, minLongitude - longitudePadding),
            Point(maxLatitude + latitudePadding / 2, maxLongitude + longitudePadding)
        )
    }
    // Временное >

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
        routeMapFrag = null
        super.onDestroyView()
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
