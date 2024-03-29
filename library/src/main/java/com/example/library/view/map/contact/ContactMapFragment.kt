package com.example.library.view.map.contact

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.java.entities.LocatedContact
import com.example.java.entities.LocationData
import com.example.library.R
import com.example.library.databinding.FragmentContactMapBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.DrawbleToBitmap.getBitmapFromVectorDrawable
import com.example.library.utils.injectViewModel
import com.example.library.viewmodel.ContactMapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import java.lang.Integer.min
import javax.inject.Inject

private const val SUGGEST_LIST_COUNT = 9

private const val ZOOM = 12f
private const val DURATION = 1f
private const val AZIMUTH = 0.0f
private const val TILT = 0.0f

private const val DEFAULT_CENTER_POSITION_LONGITUDE = 94.15
private const val DEFAULT_CENTER_POSITION_LATITUDE = 66.25
private const val BOX_SIZE = 0.2

private val center = Point(DEFAULT_CENTER_POSITION_LATITUDE, DEFAULT_CENTER_POSITION_LONGITUDE)
private val boxSize = BOX_SIZE
private val boundingBox = BoundingBox(
    Point(center.latitude - boxSize, center.longitude - boxSize),
    Point(center.latitude + boxSize, center.longitude + boxSize)
)
private val SEARCH_OPTIONS = SuggestOptions()
    .setSuggestTypes(
        SearchType.GEO.value
    )

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

    private var _contactMapFrag: FragmentContactMapBinding? = null
    private val contactMapFrag: FragmentContactMapBinding get() = _contactMapFrag!!

    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private var suggestResult: MutableList<String>? = null
    private var suggestSession: SuggestSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusContactMapContainer()
            ?.inject(this)
        contactMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _contactMapFrag = FragmentContactMapBinding.inflate(inflater, container, false)
        return contactMapFrag.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = contactMapFrag.map
        mapObjects = mapView.map.mapObjects.addCollection()

        mapView.map.setMapLoadedListener(mapLoadedListener)
        mapView.map.addInputListener(inputListener)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        suggestSession = searchManager!!.createSuggestSession()
        contactId = arguments?.getString(CONTACT_ID, "") ?: ""

        darkModeImpl()
        searchImpl()
        getContactDetails()

        contactMapFrag.saveFab.setOnClickListener { saveChangedLocationData() }
        contactMapFrag.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getContactDetails() {
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
                    contactMapFrag.apply {
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
                contactMapFrag.saveFab.isEnabled = false
                curChangedLocationData = locationData
                contactMapFrag.addressTv1.text = locationData.address
                contactMapFrag.saveFab.isEnabled = true
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

    private fun searchImpl() {
        val from = arrayOf(android.app.SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.suggest_text)
        val cursorAdapter = SimpleCursorAdapter(
            context,
            R.layout.suggestion_row,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        suggestResult = ArrayList()

        contactMapFrag.searchView.suggestionsAdapter = cursorAdapter

        contactMapFrag.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    submitQuery(query)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    requestSuggest(query)
                    val cursor = MatrixCursor(arrayOf(BaseColumns._ID, android.app.SearchManager.SUGGEST_COLUMN_TEXT_1))
                    query.let {
                        (suggestResult as ArrayList<String>).forEachIndexed { index, suggestion ->
                            if (suggestion.contains(query, true)) {
                                cursor.addRow(arrayOf(index, suggestion))
                            }
                        }
                    }
                    cursorAdapter.changeCursor(cursor)
                }
                return true
            }
        })

        contactMapFrag.searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = contactMapFrag.searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(cursor.getColumnIndex(android.app.SearchManager.SUGGEST_COLUMN_TEXT_1))
                contactMapFrag.searchView.setQuery(selection, false)

                submitQuery(contactMapFrag.searchView.query.toString())
                return true
            }
        })
    }

    private fun requestSuggest(query: String) {
        suggestSession?.suggest(
            query,
            boundingBox,
            SEARCH_OPTIONS,
            suggestListener
        )
    }

    private val suggestListener = object : SuggestSession.SuggestListener {

        override fun onResponse(suggest: MutableList<SuggestItem>) {
            suggestResult?.clear()
            for (i in 0 until min(SUGGEST_LIST_COUNT, suggest.size)) {
                suggest[i].displayText?.let { suggestResult?.add(it) }
            }
        }

        override fun onError(p0: Error) {
            TODO("Not yet implemented")
        }
    }
    private fun submitQuery(query: String) {
        searchSession = searchManager!!.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            object : Session.SearchListener {

                override fun onSearchError(p0: Error) {
                    Toast.makeText(context, getString(R.string.locationNotFound), Toast.LENGTH_SHORT).show()
                }
                override fun onSearchResponse(response: Response) {
                    val city = response.collection.children.firstOrNull()?.obj
                        ?.geometry?.get(0)?.point
                    if (city != null) {
                        mapView.map.move(
                            CameraPosition(city, ZOOM, AZIMUTH, TILT),
                            Animation(Animation.Type.SMOOTH, DURATION),
                            null
                        )
                    }
                }
            }
        )
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
        Toast.makeText(context, getString(R.string.saveLocation), Toast.LENGTH_SHORT).show()
    }

    private fun findLocatedContact(locatedContactList: List<LocatedContact>) {
        val locatedContact = locatedContactList.firstOrNull { it.id == contactId }
        if (locatedContact != null) {
            curLocatedContact = locatedContact
            contactMapFrag.apply {
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

    private val mapLoadedListener = MapLoadedListener {
        if (isNewContact) {
            Toast.makeText(context, getString(R.string.addLocation), Toast.LENGTH_SHORT).show()
        } else {
            mapView.map.move(
                CameraPosition(Point(curLocatedContact.latitude, curLocatedContact.longitude), ZOOM, AZIMUTH, TILT),
                Animation(Animation.Type.SMOOTH, DURATION),
                null
            )
        }

        if (::placemark.isInitialized) mapObjects.remove(placemark)
        placemark = mapObjects.addPlacemark(
            Point(curLocatedContact.latitude, curLocatedContact.longitude),
            ImageProvider.fromBitmap(getBitmapFromVectorDrawable(requireContext(), R.drawable.baseline_place_24))
        )
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, point: Point) {
            if (::placemark.isInitialized) mapObjects.remove(placemark)
            placemark = mapObjects.addPlacemark(
                point,
                ImageProvider.fromBitmap(getBitmapFromVectorDrawable(requireContext(), R.drawable.baseline_place_24))
            )
            contactMapViewModel.setChangedLocationData(
                LocationData(
                    longitude = point.longitude,
                    latitude = point.latitude,
                    address = getString(R.string.loading)
                )
            )
        }

        override fun onMapLongTap(p0: Map, point: Point) {
            TODO("Not yet implemented")
        }
    }
    override fun onDestroyView() {
        _contactMapFrag = null
        super.onDestroyView()
    }
    override fun onStop() {
        contactMapFrag.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapObjects.clear()
        super.onDestroy()
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        contactMapFrag.map.onStart()
        super.onStart()
    }
    companion object {
        const val CONTACT_ID = "id"

        @JvmStatic
        fun newInstance(contactId: String) =
            ContactMapFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
