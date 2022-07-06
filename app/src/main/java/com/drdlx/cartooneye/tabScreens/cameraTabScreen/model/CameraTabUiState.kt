package com.drdlx.cartooneye.tabScreens.cameraTabScreen.model

import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import com.google.ar.sceneform.ux.ArFrontFacingFragment

data class CameraTabUiState(
    val currentPictureUri: LiveData<Uri>,
    val captureButtonWorkMode: LiveData<CaptureButtonWorkMode>,
    val supportFragmentManager: LiveData<FragmentManager>,
    val arFragment: LiveData<ArFrontFacingFragment>,
)
