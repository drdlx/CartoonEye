package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components.CameraCapture
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CameraTabUiState

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraTabScreen(
    uiState: CameraTabUiState,
    setImageCallback: (Uri) -> Unit,
) {
    val imageUri = uiState.currentPictureUri.observeAsState()
    if (imageUri.value != EMPTY_IMAGE_URI) {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri.value),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    setImageCallback(EMPTY_IMAGE_URI)
                }
            ) {
                Text(stringResource(id = R.string.remove_image))
            }
        }
    } else {
        CameraCapture(
            modifier = Modifier,
            onImageFile = { file ->
                setImageCallback(file.toUri())
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraTabScreenPreview() {
    val uiState = CameraTabUiState(
        currentPictureUri = MutableLiveData(EMPTY_IMAGE_URI)
    )
    MaterialTheme {
        CameraTabScreen(
            uiState = uiState,
            setImageCallback = {},
        )
    }
}
