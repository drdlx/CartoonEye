package com.drdlx.cartooneye.mainScreens.mainScreen.view

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.rememberNavController
import com.drdlx.cartooneye.mainScreens.mainScreen.model.VoidCallback
import com.drdlx.cartooneye.mainScreens.mainScreen.navigation.TabsNavigation
import com.drdlx.cartooneye.mainScreens.mainScreen.view.components.BottomBarItem
import com.drdlx.cartooneye.mainScreens.mainScreen.view.components.BottomNavBar
import com.drdlx.cartooneye.mainScreens.mainScreen.view.components.TopBar
import com.drdlx.cartooneye.ui.theme.CartoonEyeTheme
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.ArFrontFacingFragment

@Composable
fun MainScreen(
    restartActivityCallback: VoidCallback,
    supportFragmentManager: FragmentManager?,
    arFragment: ArFrontFacingFragment?,
    toggleRecording: (ArSceneView?) -> Unit,
) {
    val currentTabVal = remember {
        MutableLiveData(BottomBarItem.CameraTabItem.route)
    }

    val currentTab = currentTabVal.observeAsState()
    val tabsNavigator = rememberNavController()

    CartoonEyeTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(
            topBar = { TopBar() },
            bottomBar = {
                BottomNavBar(
                    currentTab.value ?: BottomBarItem.CameraTabItem.route
                ) {
                    tabsNavigator.navigate(it) {
                        tabsNavigator.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                        currentTabVal.postValue(it)
                    }
                }
            },
        ) {
            Box(
                modifier = Modifier.padding(
                    PaddingValues(0.dp, 0.dp, 0.dp, it.calculateBottomPadding())
                )
            ) {
                supportFragmentManager?.let { fragmentManager ->
                    arFragment?.let { fragment ->
                        val sceneView = fragment.arSceneView
                        val screenshot = sceneView
                        TabsNavigation(
                            navController = tabsNavigator,
                            restartActivityCallback = restartActivityCallback,
                            supportFragmentManager = fragmentManager,
                            arFragment = fragment,
                            toggleRecording = toggleRecording,
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen({},null, null, {})
}
