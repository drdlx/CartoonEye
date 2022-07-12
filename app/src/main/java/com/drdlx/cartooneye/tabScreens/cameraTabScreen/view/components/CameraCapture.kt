package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.R.*
import com.drdlx.cartooneye.mainScreens.mainScreen.model.VoidCallback
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CaptureButtonWorkMode
import com.drdlx.cartooneye.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.ArFrontFacingFragment
import kotlinx.coroutines.launch
import java.io.File
import java.nio.IntBuffer


private const val TAG = "CameraCapture"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCapture(
    modifier: Modifier = Modifier,
    captureImageCallback: (ArSceneView) -> Unit,
    arFragment: ArFrontFacingFragment,
    supportFragmentManager: FragmentManager,
    toggleCameraMode: VoidCallback,
    toggleRecording: (ArSceneView?) -> Unit,
    captureButtonWorkMode: CaptureButtonWorkMode,
) {
    val context = LocalContext.current
    Permission(
        Manifest.permission.CAMERA,
        rationale = stringResource(id = string.camera_permission_ask_message),
        permissionsNotAvailableContent = {
            Column(Modifier) {
                Text(stringResource(id = string.no_camera_message))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text(stringResource(id = string.open_settings))
                }
            }
        }
    ) {

        Box(modifier = modifier) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                supportFragmentManager = supportFragmentManager,
                arFragment = arFragment,
            )

            CapturePictureButton(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                onClick = {
                    when(captureButtonWorkMode) {
                        CaptureButtonWorkMode.PHOTO -> captureImageCallback(arFragment.arSceneView)
                        CaptureButtonWorkMode.VIDEO -> toggleRecording(arFragment.arSceneView)
                        else -> Toast.makeText(context, "Please start AR Session first", Toast.LENGTH_LONG).show()
                    }
                }
            )

            Button(modifier = Modifier.align(Alignment.BottomEnd), onClick = {
                toggleCameraMode()
            }
            ) {
                Text(
                    stringResource(id = when (captureButtonWorkMode) {
                        CaptureButtonWorkMode.PHOTO -> string.switch_to_video
                        CaptureButtonWorkMode.VIDEO -> string.switch_to_photo
                        else -> string.start_ar_session
                    })
                )

            }
        }
    }
}
