package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components.CameraCapture
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.mainScreens.mainScreen.model.VoidCallback
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CameraTabUiState
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CaptureButtonWorkMode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.ux.ArFrontFacingFragment

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraTabScreen(
    uiState: CameraTabUiState,
    setImageCallback: (Uri) -> Unit,
    saveImageCallback: (Uri, Context) -> Unit,
    captureImageCallback: (ArSceneView) -> Unit,
    initDataCallback: VoidCallback,
    toggleRecording: (ArSceneView?) -> Unit,
    toggleCameraMode: VoidCallback,
    getCommitFunction: (
        fragment: Fragment,
        tag: String
    ) -> (FragmentTransaction.(containerId: Int) -> Unit)
) {
    val imageUri = uiState.currentPictureUri.observeAsState()
    val captureButtonWorkMode =
        uiState.captureButtonWorkMode.observeAsState(CaptureButtonWorkMode.PHOTO)
    if (imageUri.value != EMPTY_IMAGE_URI) {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri.value),
                contentDescription = "Captured image"
            )
            Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                Button(onClick = {
                    setImageCallback(EMPTY_IMAGE_URI)
                }) {
                    Text(stringResource(id = R.string.remove_image))
                }
                val localContext = LocalContext.current
                Button(
                    onClick = {
                        imageUri.value?.let {
                            saveImageCallback(it, localContext)
                        }
                        setImageCallback(EMPTY_IMAGE_URI)
                        Toast
                            .makeText(localContext, "Photo has been saved!", Toast.LENGTH_LONG)
                            .show()
                    }
                ) {
                    Text(stringResource(id = R.string.save_image))
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CameraCapture(
                modifier = Modifier,
                captureImageCallback = captureImageCallback,
                toggleRecording = toggleRecording,
                captureButtonWorkMode = captureButtonWorkMode.value,
                toggleCameraMode = toggleCameraMode,
                getCommitFunction = getCommitFunction
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraTabScreenPreview() {
    val uiState = CameraTabUiState(
        currentPictureUri = MutableLiveData(EMPTY_IMAGE_URI),
        captureButtonWorkMode = MutableLiveData(CaptureButtonWorkMode.PHOTO),
        supportFragmentManager = MutableLiveData(null),
        arFragment = MutableLiveData(null),
    )
    MaterialTheme {
        CameraTabScreen(
            uiState = uiState,
            setImageCallback = {},
            saveImageCallback = { _: Uri, _: Context -> },
            captureImageCallback = { },
            toggleRecording = {},
            toggleCameraMode = {},
            initDataCallback = {},
            getCommitFunction = { _, _ -> {} }
        )
    }
}

