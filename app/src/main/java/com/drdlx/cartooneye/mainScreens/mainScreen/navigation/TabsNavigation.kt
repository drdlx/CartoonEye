package com.drdlx.cartooneye.mainScreens.mainScreen.navigation

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.drdlx.cartooneye.navigation.routeObjects.MainScreenTabRoute
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.CameraTabScreen
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.viewModel.CameraTabViewModel
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.view.GalleryTabScreen
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel.GalleryTabViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun TabsNavigation(
    navController: NavHostController,
    surfaceView: GLSurfaceView?,
    renderer: GLSurfaceView.Renderer,
    saveImageCallback: () -> Unit
) {
    NavHost(navController, startDestination = MainScreenTabRoute.CameraTab.name) {
        composable(route = MainScreenTabRoute.CameraTab.name) {
            val viewModel = getViewModel<CameraTabViewModel>()
            CameraTabScreen(
                uiState = viewModel.uiState,
                setImageCallback = viewModel::changeCurrentPicture,
                saveImageCallback = saveImageCallback,
                surfaceView = surfaceView,
                renderer = renderer
            )
        }
        composable(route = MainScreenTabRoute.GalleryTab.name) {
            val viewModel = getViewModel<GalleryTabViewModel>()
            GalleryTabScreen(
                uiState = viewModel.uiState,
                setImageCallback = viewModel::changeCurrentPicture,
                saveImageCallback = viewModel::saveCurrentPicture,
            )
        }
    }
}
