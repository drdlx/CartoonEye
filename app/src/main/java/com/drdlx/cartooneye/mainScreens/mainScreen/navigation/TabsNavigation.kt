package com.drdlx.cartooneye.mainScreens.mainScreen.navigation

import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.drdlx.cartooneye.mainScreens.mainScreen.model.VoidCallback
import com.drdlx.cartooneye.navigation.routeObjects.MainScreenTabRoute
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.CameraTabScreen
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.viewModel.CameraTabViewModel
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.view.GalleryTabScreen
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel.GalleryTabViewModel
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.ux.ArFrontFacingFragment
import org.koin.androidx.compose.getViewModel

@Composable
fun TabsNavigation(
    navController: NavHostController,
    restartActivityCallback: VoidCallback,
    arFragment: ArFrontFacingFragment,
    supportFragmentManager: FragmentManager,
    toggleRecording: (ArSceneView?) -> Unit,
) {
    NavHost(navController, startDestination = MainScreenTabRoute.CameraTab.name) {
        composable(route = MainScreenTabRoute.CameraTab.name) {
            val viewModel = getViewModel<CameraTabViewModel>()
            CameraTabScreen(
                uiState = viewModel.uiState,
                setImageCallback = viewModel::changeCurrentPicture,
                saveImageCallback = viewModel::saveCurrentPicture,
                captureImageCallback = viewModel::captureImage,
                restartActivityCallback = restartActivityCallback,
                supportFragmentManager = supportFragmentManager,
                arFragment = arFragment,
                toggleRecording = toggleRecording,
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
