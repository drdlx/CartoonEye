package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.provider.Settings
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
import com.drdlx.cartooneye.mainScreens.mainScreen.model.VoidCallback
import com.drdlx.cartooneye.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.ar.sceneform.ArSceneView
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
    recordingVideoCallback: VoidCallback,
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
                    captureImageCallback(arFragment.arSceneView)
                    /*surfaceView?.queueEvent {
                        val mWidth = UtilsStorage.arWidth.value
                        val mHeight = UtilsStorage.arHeight.value
                        val pixelData = IntArray(mWidth!! * mHeight!!)

                        // Read the pixels from the current GL frame.
                        val buf: IntBuffer = IntBuffer.wrap(pixelData)
                        buf.position(0)
                        GLES20.glReadPixels(
                            0, 0, mWidth, mHeight,
                            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf
                        )

                        val bitmapData = IntArray(pixelData.size)
                        for (i in 0 until mHeight) {
                            for (j in 0 until mWidth) {
                                val p = pixelData[i * mWidth + j]
                                val b = p and 0x00ff0000 shr 16
                                val r = p and 0x000000ff shl 16
                                val ga = p and -0xff0100
                                bitmapData[(mHeight - i - 1) * mWidth + j] = ga or r or b
                            }
                        }
                        // Create a bitmap.
                        val bmp = Bitmap.createBitmap(
                            bitmapData,
                            mWidth, mHeight, Bitmap.Config.ARGB_8888
                        )

                        coroutineScope.launch {
                            makeTemporaryPicture(bmp).also(onImageFile)
                        }
                    }*/
                }
            )
        }
    }
}
