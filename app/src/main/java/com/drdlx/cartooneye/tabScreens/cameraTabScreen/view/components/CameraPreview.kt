package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.opengl.GLSurfaceView
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.drdlx.cartooneye.utils.getCameraProvider
import kotlinx.coroutines.launch
import android.util.Log
import androidx.camera.core.UseCase
import androidx.compose.material.contentColorFor
import androidx.compose.ui.platform.LocalContext
import com.drdlx.cartooneye.utils.ArActivityStorage

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onUseCase: (UseCase) -> Unit = { }
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

            ArActivityStorage.initRenderer(localContext)

            val surfaceView = GLSurfaceView(context).apply {

                this.setRenderer(ArActivityStorage.arRenderer.value)

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

//                ArActivityStorage.initRenderer(context)

                println("WOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOW")
                println(ArActivityStorage.arActivity.value)
                println(ArActivityStorage.arRenderer.value)
                println("WOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOW")
                /*ArActivityStorage.arRenderer.value?.let { facesRenderer ->
                    println(facesRenderer)
//                    facesRenderer.initSession()
                    this.setRenderer(facesRenderer)
                }*/
            }

            /*onUseCase(
                Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
            )*/
//            previewView
            surfaceView
        },
        update = {
//            println("Renderer on update: ${ArActivityStorage.arRenderer.value}")
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
