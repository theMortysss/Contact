package com.example.library.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.library.R
import com.example.library.databinding.FragmentMainBinding
import com.example.library.utils.Constants
import com.example.library.view.contact.ContactDetailsFragment

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"
class MainFragment : Fragment(R.layout.fragment_main) {

    private var _mainFrag: FragmentMainBinding? = null
    private val mainFrag: FragmentMainBinding get() = _mainFrag!!

    private val curIntent by lazy(LazyThreadSafetyMode.NONE) { requireActivity().intent }
    private val activityStartedByNotification by lazy(LazyThreadSafetyMode.NONE) {
        curIntent.action != LAUNCHER_START_INTENT
    }
    private var check = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mainFrag = FragmentMainBinding.inflate(inflater, container, false)
        return mainFrag.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView = mainFrag.mainBottomNavigation
        val navController = (
            childFragmentManager
                .findFragmentById(R.id.mainContainerView) as NavHostFragment
            )
            .navController
        bottomNavigationView.setupWithNavController(navController)

        if (activityStartedByNotification and check) {
            val contactId = curIntent.getStringExtra(Constants.CONTACT_ID)
            navToDetails(contactId, activityStartedByNotification, check)
            check = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mainFrag = null
    }
    private fun navToDetails(contactId: String?, startType: Boolean, check: Boolean) {
        findNavController().navigate(
            R.id.action_mainFragment_to_contactDetailsFragment2,
            bundleOf(
                ContactDetailsFragment.CONTACT_ID to contactId,
                ContactDetailsFragment.START_TYPE to startType,
                ContactDetailsFragment.CHECK to check
            )
        )
    }
}
