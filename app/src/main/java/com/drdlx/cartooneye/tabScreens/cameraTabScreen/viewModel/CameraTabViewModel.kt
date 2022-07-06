package com.drdlx.cartooneye.tabScreens.cameraTabScreen.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.PixelCopy
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CameraTabUiState
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import com.google.ar.sceneform.ArSceneView
import org.koin.android.annotation.KoinViewModel
import android.util.Log
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewModelScope
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CaptureButtonWorkMode
import com.drdlx.cartooneye.utils.makeTemporaryPicture
import com.google.ar.sceneform.ux.ArFrontFacingFragment
import kotlinx.coroutines.launch

@KoinViewModel
class CameraTabViewModel(
    private val filesIOService: FilesIOService,
) : ViewModel() {

    companion object {
        private const val TAG = "CameraTabViewModel"
    }

    private val currentPictureUri = MutableLiveData(EMPTY_IMAGE_URI)
    private val captureButtonWorkMode = MutableLiveData(CaptureButtonWorkMode.PHOTO)
    private val supportFragmentManager = MutableLiveData<FragmentManager>(null)
    private val arFragment = MutableLiveData<ArFrontFacingFragment>(null)

    val uiState = CameraTabUiState(
        currentPictureUri = currentPictureUri,
        captureButtonWorkMode = captureButtonWorkMode,
        arFragment = arFragment,
        supportFragmentManager = supportFragmentManager,
    )

    fun changeCurrentPicture(uri: Uri) {
        currentPictureUri.value = uri
    }

    private fun changeArFragmentManager(fragmentManager: FragmentManager) {
        supportFragmentManager.postValue(fragmentManager)
    }

    private fun changeArFragment(fragment: ArFrontFacingFragment) {
        arFragment.postValue(fragment)
    }

    fun initArElements(fragmentManager: FragmentManager, arFragment: ArFrontFacingFragment) {
        changeArFragmentManager(fragmentManager)
        changeArFragment(arFragment)
    }

    fun changeCameraMode(mode: CaptureButtonWorkMode) {
        captureButtonWorkMode.value = mode
    }

    fun toggleCameraMode() = when (captureButtonWorkMode.value) {
        CaptureButtonWorkMode.PHOTO -> {
            captureButtonWorkMode.value = CaptureButtonWorkMode.VIDEO
        }
        else -> {
            captureButtonWorkMode.value = CaptureButtonWorkMode.PHOTO
        }
    }

    fun saveCurrentPicture(uri: Uri, context: Context) {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        filesIOService.savePhoto(bitmap, context.contentResolver)
    }

    fun captureImage(arSceneView: ArSceneView) {

        val bitmap = Bitmap.createBitmap(
            arSceneView.width,
            arSceneView.height,
            Bitmap.Config.ARGB_8888
        )
        PixelCopy.request(
            arSceneView, bitmap, { result ->
                when (result) {
                    PixelCopy.SUCCESS -> {
                        viewModelScope.launch {
                            val tempFile = makeTemporaryPicture(bitmap)
                            changeCurrentPicture(tempFile.toUri())
                        }
                    }
                    else -> Log.e(TAG, "Screenshot failure: $result")
                }
            }, Handler(
                HandlerThread("screenshot")
                    .apply { start() }.looper
            )
        )
    }

}
