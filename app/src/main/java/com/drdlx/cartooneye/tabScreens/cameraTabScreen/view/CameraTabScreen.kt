package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components.CameraCapture
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraTabScreen() {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }

    if (imageUri != EMPTY_IMAGE_URI) {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = EMPTY_IMAGE_URI
                }
            ) {
                Text("Remove image")
            }
        }
    } else {
        CameraCapture(
            modifier = Modifier,
            onImageFile = { file ->
                imageUri = file.toUri()
            }
        )
    }
}
