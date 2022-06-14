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
//    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onUseCase: (ARFacesRenderer) -> Unit = { },
    renderer: GLSurfaceView.Renderer,
    surfaceView: GLSurfaceView?,
    session: Session?,
) {

    val localContext = LocalContext.current.applicationContext
    AndroidView(
        modifier = modifier,
        factory = { context ->
            /*val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }*/

//            ArActivityStorage.initRenderer(localContext)

            val surfaceView1 = GLSurfaceView(context).apply {

//                this.setRenderer(renderer)


                // Set up renderer.
                this.preserveEGLContextOnPause = true
                this.setEGLContextClientVersion(2)
                this.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.

                this.setRenderer(renderer)
                this.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                this.setWillNotDraw(false)

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            /*onUseCase(
                Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
            )*/

            /*onUseCase(
                ARFacesRenderer(localContext, ArActivityStorage.arActivity.value!!)
            )*/

//            previewView
            surfaceView1
        },
        update = {
//            println("Renderer on update: ${ArActivityStorage.arRenderer.value}")
            it.onResume()
            println("Update")
            /*if (ArActivityStorage.arRenderer.value == null) {
                ArActivityStorage.initRenderer(localContext)
                println("Created renderer on update: ${ArActivityStorage.arRenderer.value}")
            }
            ArActivityStorage.arRenderer.value?.let { facesRenderer ->
//                it.onPause()
                facesRenderer.initSession()
                it.setRenderer(facesRenderer)
                it.onResume()
            }*/
        }
    )
}
