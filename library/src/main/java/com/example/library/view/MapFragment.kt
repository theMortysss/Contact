package com.example.library.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.library.R
import com.example.library.databinding.FragmentMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory

class MapFragment : Fragment(R.layout.fragment_map) {

    private var _mapFrag: FragmentMapBinding? = null
    private val mapFrag: FragmentMapBinding get() = _mapFrag!!

    override fun onCreate(savedInstanceState: Bundle?) {
        TransportFactory.initialize(activity)
        MapKitFactory.initialize(activity)
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mapFrag = FragmentMapBinding.inflate(inflater, container, false)
        return mapFrag.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView = mapFrag.mapBottomNavigation
        bottomNavigationView.setBackgroundResource(R.color.colorPrimary)
        val navController = (
                childFragmentManager
                    .findFragmentById(R.id.mapContainerView) as NavHostFragment
                )
            .navController
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mapFrag = null
    }
}
