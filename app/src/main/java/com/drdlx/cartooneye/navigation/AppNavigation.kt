package com.drdlx.cartooneye.navigation

import com.drdlx.cartooneye.navigation.routeObjects.AppScreens
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppNavigation {
    private val _navRoute =
        MutableSharedFlow<AppScreens>(extraBufferCapacity = 1)
    val navRoute = _navRoute.asSharedFlow()

    fun navigateTo(destination: AppScreens) {
        _navRoute.tryEmit(destination)
    }

}