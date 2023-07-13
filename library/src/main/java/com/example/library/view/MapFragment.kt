package com.example.library.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.library.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory

class MapFragment : Fragment(R.layout.fragment_map) {

    override fun onCreate(savedInstanceState: Bundle?) {
        TransportFactory.initialize(requireActivity())
        MapKitFactory.initialize(requireActivity())
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.mapBottomNavigation)
        val navController = (childFragmentManager.findFragmentById(R.id.mapContainerView) as NavHostFragment)
            .navController
        bottomNavigationView.setupWithNavController(navController)
    }
}
