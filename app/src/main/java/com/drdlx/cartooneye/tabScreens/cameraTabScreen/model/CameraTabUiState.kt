package com.drdlx.cartooneye.tabScreens.cameraTabScreen.model

import android.net.Uri
import androidx.lifecycle.LiveData

data class CameraTabUiState(
    val currentPictureUri: LiveData<Uri>,
    val captureButtonWorkMode: LiveData<CaptureButtonWorkMode>,
)
