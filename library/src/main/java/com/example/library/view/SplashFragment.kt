package com.example.library.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.library.R
import com.example.library.utils.Constants
import com.example.library.utils.Constants.TAG
import com.example.library.view.contact.ContactDetailsFragment

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val curIntent by lazy(LazyThreadSafetyMode.NONE) { requireActivity().intent }
    private val activityStartedByNotification by lazy(LazyThreadSafetyMode.NONE) {
        curIntent.action != LAUNCHER_START_INTENT
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.READ_CONTACTS)
        } else if (activityStartedByNotification) {
            val contactId = curIntent.getStringExtra(Constants.CONTACT_ID)
            findNavController().navigate(
                R.id.action_helloFragment_to_contactDetailsFragment2,
                bundleOf(ContactDetailsFragment.CONTACT_ID to contactId)
            )
        } else view.postDelayed({
            findNavController().navigate(R.id.action_helloFragment_to_mainFragment)
        }, 2000)
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) { // Do something if permission granted
                Log.d(TAG, "permission granted by the user")
                findNavController().navigate(R.id.action_helloFragment_to_mainFragment)
            } else { // Do something as the permission is not granted
                Log.d(TAG, "permission denied by the user")
            }
        }
}
