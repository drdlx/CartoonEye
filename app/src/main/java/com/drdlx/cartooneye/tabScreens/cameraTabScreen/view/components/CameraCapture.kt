package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.opengl.GLSurfaceView
import android.provider.Settings
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drdlx.cartooneye.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCapture(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onImageFile: (File) -> Unit = { },
    surfaceView: GLSurfaceView?,
    renderer: GLSurfaceView.Renderer,
    takePictureCallback: () -> Unit
) {
    val context = LocalContext.current
    Permission(
        Manifest.permission.CAMERA,
        rationale = stringResource(id = com.drdlx.cartooneye.R.string.camera_permission_ask_message),
        permissionsNotAvailableContent = {
            Column(Modifier) {
                Text(stringResource(id = com.drdlx.cartooneye.R.string.no_camera_message))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text(stringResource(id = com.drdlx.cartooneye.R.string.open_settings))
                }
            }
        }
    ) {

        Box(modifier = modifier) {
//            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()
//            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }

            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder()
                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                )
            }

            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                renderer = renderer,
                onUseCase = {
//                    renderer = it
//                    previewUseCase = it
                },
                surfaceView = surfaceView,
                session = null,
            )
            CapturePictureButton(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                onClick = {
                    coroutineScope.launch {
                        takePictureCallback()
                        /*imageCaptureUseCase.takePicture(executor = context.executor).also {
                            onImageFile(it)
                        }*/
                    }
                }
            )
//            LaunchedEffect(renderer) {
//                renderer.destroySession()
//                renderer.initSession()
//                renderer.configSession()
                /*val cameraProvider = context.getCameraProvider()
                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (exception: Exception) {
                    Log.e("CameraCapture", "Failed to bind camera use cases", exception)
                }*/
//            }
        }
    }
}