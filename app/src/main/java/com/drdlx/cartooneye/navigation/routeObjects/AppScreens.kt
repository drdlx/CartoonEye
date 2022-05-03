package com.drdlx.cartooneye.navigation.routeObjects

import androidx.navigation.NavOptions

const val cameraScreenRoute = "CameraScreenRoute"
const val popRouteName = "popRoute"

sealed class AppScreens(
    val route: String,
    val options: NavOptions? = null,
    val inclusive: Boolean = false,
    val saveState: Boolean = false,
    val popTargetRoute: String = "",
) {
    object CameraScreen: AppScreens(cameraScreenRoute)
    object GalleryScreen: AppScreens(cameraScreenRoute)
}
