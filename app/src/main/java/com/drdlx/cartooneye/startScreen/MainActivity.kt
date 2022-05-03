package com.drdlx.cartooneye.startScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drdlx.cartooneye.consts.defaultRoute
import com.drdlx.cartooneye.mainScreen.view.MainScreen
import com.drdlx.cartooneye.navigation.AppNavigation
import com.drdlx.cartooneye.navigation.routeObjects.AppScreens
import com.drdlx.cartooneye.navigation.routeObjects.popRouteName
import com.drdlx.cartooneye.ui.theme.CartoonEyeTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    companion object {
        private const val launchEffectName = "Navigator"
    }

    //inject it
    private val navigator: AppNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startingDestination = AppScreens.CameraScreen.route
        val startingGraph = defaultRoute

        setContent {

            val navigationController = rememberNavController()

            LaunchedEffect(launchEffectName) {

                navigator.navRoute.onEach {
                    when (it.route) {
                        popRouteName -> {
                            when (it.popTargetRoute.isEmpty()) {
                                true -> navigationController.popBackStack()
                                false -> navigationController.popBackStack(
                                    it.popTargetRoute,
                                    it.inclusive,
                                    it.saveState
                                )
                            }
                        }
                        else -> navigationController.navigate(it.route, it.options)
                    }
                }.launchIn(this)
            }

            NavHost(
                navController = navigationController,
                startDestination = AppScreens.CameraScreen.route
            ) {
                composable(route = AppScreens.CameraScreen.route) {
                    MainScreen()
                }
                composable(route = AppScreens.CameraScreen.route) {
                    MainScreen()
                }

            }

        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    CartoonEyeTheme {
        Greeting("Android")
    }
}