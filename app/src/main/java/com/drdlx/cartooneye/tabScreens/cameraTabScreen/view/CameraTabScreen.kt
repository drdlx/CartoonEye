package com.drdlx.cartooneye.tabScreens.cameraTabScreen.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components.CameraCapture
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.view.components.CameraPreview
import com.drdlx.cartooneye.utils.Permission
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraTabScreen() {
    val emptyImageUri = Uri.parse("file://dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }

    if (imageUri != emptyImageUri) {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = emptyImageUri
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
