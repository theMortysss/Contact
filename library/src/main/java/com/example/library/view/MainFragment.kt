package com.example.library.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.library.R
import com.example.library.utils.Constants
import com.example.library.view.contact.ContactDetailsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"
class MainFragment : Fragment(R.layout.fragment_main) {

    private val curIntent by lazy(LazyThreadSafetyMode.NONE) { requireActivity().intent }
    private val activityStartedByNotification by lazy(LazyThreadSafetyMode.NONE) {
        curIntent.action != LAUNCHER_START_INTENT
    }
    private var check = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.mainBottomNavigation)
        val navController = (childFragmentManager.findFragmentById(R.id.mainContainerView) as NavHostFragment)
            .navController
        bottomNavigationView.setupWithNavController(navController)

        if (activityStartedByNotification and check) {
            check = false
            val contactId = curIntent.getStringExtra(Constants.CONTACT_ID)
            navToDetails(contactId)
        }
    }
    private fun navToDetails(contactId: String?) {
        findNavController().navigate(
            R.id.action_mainFragment_to_contactDetailsFragment2,
            bundleOf(ContactDetailsFragment.CONTACT_ID to contactId)
        )
    }
}
