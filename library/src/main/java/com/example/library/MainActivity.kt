package com.example.library

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.library.utils.Constants.CONTACT_ID
import com.example.library.utils.Constants.MAPKIT_API_KEY
import com.example.library.utils.PermissionsAccessHelper.isPermissionsRequestSuccessful
import com.example.library.utils.PermissionsAccessHelper.startPermissionsRequest
import com.example.library.view.contact.ContactDetailsFragment
import com.example.library.view.contacts.ContactListFragment
import com.example.library.view.contacts.OnContactListCallback
import com.example.library.view.map.contact.ContactMapFragment
import com.example.library.view.map.contact.OnContactMapCallback
import com.example.library.view.map.everybody.EverybodyMapFragment
import com.example.library.view.map.everybody.OnEverybodyMapCallback
import com.example.library.view.map.route.OnRouteMapCallback
import com.example.library.view.map.route.RouteMapFragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"

class MainActivity : AppCompatActivity(), OnContactListCallback, OnContactMapCallback,
    OnEverybodyMapCallback, OnRouteMapCallback {

    private val askPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS
    )
    private val curIntent by lazy(LazyThreadSafetyMode.NONE) { intent }
    private val activityStartedByNotification by lazy(LazyThreadSafetyMode.NONE) {
        curIntent.action != LAUNCHER_START_INTENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        TransportFactory.initialize(this)
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(toolbar))
        if (savedInstanceState == null) {
            startPermissionsRequest(
                activity = this,
                permissions = askPermissions,
                showIntro = true
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Проверить даны ли запрошенные ранее разрешения пользователем
        if (isPermissionsRequestSuccessful(
                activity = this,
                showEpilogue = true
            )
        ) {
            chooseNavigation()
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.everybodyToolbarBtn) {
//            navigateToBaseMapFragment("", MapScreenMode.EVERYBODY)
//        }
//        return super.onOptionsItemSelected(item)
//    }


    private fun chooseNavigation() {
        if (!activityStartedByNotification) {
            navigateToContactListFragment()
        } else {
            val contactId = curIntent.getStringExtra(CONTACT_ID)
            Log.d(com.example.library.utils.Constants.TAG, "MainActivity: contactId = $contactId")
            if (!contactId.isNullOrEmpty()) {
                navigateToContactDetailsFragment(contactId)
            }
        }
    }

    private fun navigateToContactListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frag_container,
                ContactListFragment()
            )
            .commit()
    }

    override fun navigateToContactDetailsFragment(contactId: String) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(
                R.id.frag_container,
                ContactDetailsFragment.newInstance(contactId),
                ContactDetailsFragment.FRAGMENT_NAME
            )
        if (!activityStartedByNotification)
            transaction.addToBackStack(ContactDetailsFragment.FRAGMENT_NAME)
        transaction.commit()
    }

    override fun navigateToContactMapFragment(contactId: String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frag_container,
                ContactMapFragment.newInstance(contactId),
                ContactMapFragment.FRAGMENT_NAME
            )
            .addToBackStack(ContactMapFragment.FRAGMENT_NAME)
            .commit()
    }

    override fun navigateToEverybodyMapFragment(contactId: String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frag_container,
                EverybodyMapFragment.newInstance(contactId),
                EverybodyMapFragment.FRAGMENT_NAME
            )
            .addToBackStack(EverybodyMapFragment.FRAGMENT_NAME)
            .commit()
    }

    override fun navigateToRouteMapFragment(contactId: String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frag_container,
                RouteMapFragment.newInstance(contactId),
                RouteMapFragment.FRAGMENT_NAME
            )
            .addToBackStack(RouteMapFragment.FRAGMENT_NAME)
            .commit()
    }

}
