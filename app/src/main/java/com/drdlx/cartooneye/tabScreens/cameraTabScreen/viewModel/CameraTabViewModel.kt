package com.drdlx.cartooneye.tabScreens.cameraTabScreen.viewModel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.media.CamcorderProfile
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.drdlx.cartooneye.startScreen.MainActivity
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CaptureButtonWorkMode
import com.drdlx.cartooneye.utils.VideoRecorder
import com.drdlx.cartooneye.utils.makeTemporaryPicture
import com.google.ar.sceneform.ux.ArFragment
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

    val uiState = CameraTabUiState(
        currentPictureUri = currentPictureUri,
        captureButtonWorkMode = captureButtonWorkMode
    )

    fun changeCurrentPicture(uri: Uri) {
        currentPictureUri.value = uri
    }

    fun changeCameraMode(mode: CaptureButtonWorkMode) {
        captureButtonWorkMode.value = mode
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
