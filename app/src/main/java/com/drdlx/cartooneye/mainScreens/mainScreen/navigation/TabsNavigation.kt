package com.drdlx.cartooneye.mainScreens.mainScreen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.drdlx.cartooneye.navigation.routeObjects.MainScreenTabRoute
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.CameraTabScreen
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.view.GalleryTabScreen

@Composable
fun TabsNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = MainScreenTabRoute.CameraTab.name) {
        composable(route = MainScreenTabRoute.CameraTab.name) {
            CameraTabScreen()
        }
        composable(route = MainScreenTabRoute.GalleryTab.name) {
            GalleryTabScreen()
        }
    }
}