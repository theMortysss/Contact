package com.example.library.di

import com.example.library.view.map.route.RouteMapFragment

interface RouteMapContainer {

    fun inject(routeMapFragment: RouteMapFragment)
}
