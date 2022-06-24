package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.opengl.GLSurfaceView
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.drdlx.cartooneye.utils.ARFacesRenderer
import com.google.ar.core.Session

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    renderer: GLSurfaceView.Renderer,
    surfaceView: GLSurfaceView?,
) {

    val localContext = LocalContext.current.applicationContext
    if (surfaceView != null) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                
                surfaceView.apply {
                    // Set up renderer.
                    this.preserveEGLContextOnPause = true
                    this.setEGLContextClientVersion(2)
                    this.setEGLConfigChooser(
                        8, 8, 8, 8, 16, 0
                    ) // Alpha used for plane blending.

                    this.setRenderer(renderer)
                    this.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                    this.setWillNotDraw(false)

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                surfaceView
            },
            update = {
                it.onResume()
                println("Update")

            }
        )
    }
}
