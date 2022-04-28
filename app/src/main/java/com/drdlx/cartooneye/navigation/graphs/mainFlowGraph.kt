package com.drdlx.cartooneye.navigation.graphs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.drdlx.cartooneye.consts.defaultRoute
import com.drdlx.cartooneye.mainScreens.MainScreen
import com.drdlx.cartooneye.navigation.routeObjects.AppScreens

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.mainFlowGraph(navController: NavController) {
    navigation(startDestination = AppScreens.StartingScreen.route, route = defaultRoute) {
        composable(route = AppScreens.StartingScreen.route) {
            MainScreen()
        }
    }
}