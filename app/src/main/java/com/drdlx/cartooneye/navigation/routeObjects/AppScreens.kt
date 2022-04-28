package com.drdlx.cartooneye.navigation.routeObjects

import androidx.navigation.NavOptions

const val startingScreenRoute = "StartingScreenRoute"
const val popRouteName = "popRoute"

sealed class AppScreens(
    val route: String,
    val options: NavOptions? = null,
    val inclusive: Boolean = false,
    val saveState: Boolean = false,
    val popTargetRoute: String = "",
) {
    object StartingScreen: AppScreens(startingScreenRoute)
}
