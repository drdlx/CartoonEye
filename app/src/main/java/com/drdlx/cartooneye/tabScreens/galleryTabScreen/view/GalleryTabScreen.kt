package com.drdlx.cartooneye.tabScreens.galleryTabScreen.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.drdlx.cartooneye.R
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CameraTabUiState
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.model.GalleryTabUiState
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.view.components.GallerySelect
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI

@OptIn(ExperimentalCoilApi::class)
@Composable
fun GalleryTabScreen(
    uiState: GalleryTabUiState,
    setImageCallback: (Uri) -> Unit,
) {
    val imageUri = uiState.currentPictureUri.observeAsState()

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
                    setImageCallback(EMPTY_IMAGE_URI)
                }
            ) {
                Text(stringResource(id = R.string.remove_image))
            }
        }
    } else {
        GallerySelect(
            modifier = Modifier,
            onImageUri = { file ->
                setImageCallback(file)
            }
        )
    }
}
