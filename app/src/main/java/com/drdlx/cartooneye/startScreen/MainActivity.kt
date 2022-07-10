package com.drdlx.cartooneye.startScreen

import android.content.ContentValues
import android.media.CamcorderProfile
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.mainScreens.mainScreen.view.MainScreen
import com.drdlx.cartooneye.navigation.AppNavigation
import com.drdlx.cartooneye.navigation.routeObjects.AppScreens
import com.drdlx.cartooneye.navigation.routeObjects.popRouteName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject


class MainActivity : FragmentActivity() {

    companion object {
        private const val launchEffectName = "Navigator"
        private const val TAG = "MainActivity"
    }

    private val navigator: AppNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
                    MainScreen(
                        toggleRecording = { },
                        getCommitFunction = ::getCommitFunction
                    )
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getCommitFunction(
        fragment : Fragment,
        tag: String
    ): FragmentTransaction.(containerId: Int) -> Unit =  { replace(it, fragment, tag) }

    /*
   * Used as a handler for onClick, so the signature must match onClickListener.
   */
    /*private fun toggleRecording(unusedView: ArSceneView?) {

        // TODO add write permission check
        unusedView?.let {
            val recording = videoRecorder.onToggleRecord(it)
            if (!recording) {
                val videoPath = videoRecorder.videoPath!!.absolutePath
                Toast.makeText(this, "Video saved: $videoPath", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Video saved: $videoPath")

                // Send  notification of updated content.
                val values = ContentValues()
                values.put(MediaStore.Video.Media.TITLE, "Sceneform Video")
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                values.put(MediaStore.Video.Media.DATA, videoPath)
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }*/


}
