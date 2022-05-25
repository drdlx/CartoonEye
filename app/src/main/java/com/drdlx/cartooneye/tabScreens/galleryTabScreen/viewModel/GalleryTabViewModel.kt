package com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drdlx.cartooneye.services.files.FilesIOService
import com.drdlx.cartooneye.tabScreens.cameraTabScreen.model.CameraTabUiState
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.model.GalleryTabUiState
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class GalleryTabViewModel(
    private val filesIOService: FilesIOService,
) : ViewModel() {

    private val currentPictureUri = MutableLiveData(EMPTY_IMAGE_URI)

    val uiState = GalleryTabUiState(
        currentPictureUri = currentPictureUri
    )

    fun changeCurrentPicture(uri: Uri) {
        currentPictureUri.value = uri
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

}