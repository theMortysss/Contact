package com.example.library

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.library.utils.Constants.CONTACT_ID
import com.example.library.utils.Constants.MAPKIT_API_KEY
import com.example.library.utils.PermissionsAccessHelper.isPermissionsRequestSuccessful
import com.example.library.utils.PermissionsAccessHelper.startPermissionsRequest
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"

class MainActivity : AppCompatActivity() {

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
        if (savedInstanceState == null) {
            startPermissionsRequest(
                activity = this,
                permissions = askPermissions,
                showIntro = true
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
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

    private fun chooseNavigation() {
        if (!activityStartedByNotification) {
            // navigateToContactListFragment()
        } else {
            val contactId = curIntent.getStringExtra(CONTACT_ID)
            if (!contactId.isNullOrEmpty()) {
                // navigateToContactDetailsFragment(contactId)
            }
        }
    }
}
