package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.content.Intent
import android.content.Intent.getIntent
import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    surfaceView: GLSurfaceView?,
) {

    if (surfaceView != null) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                println("Created surface view")
                surfaceView
            },
            update = {
                println("Update")
            }
        )
    }
}
