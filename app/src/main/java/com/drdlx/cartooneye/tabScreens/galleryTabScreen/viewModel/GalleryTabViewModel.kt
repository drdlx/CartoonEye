package com.drdlx.cartooneye.tabScreens.galleryTabScreen.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drdlx.cartooneye.tabScreens.galleryTabScreen.model.GalleryTabUiState
import com.drdlx.cartooneye.utils.EMPTY_IMAGE_URI
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class GalleryTabViewModel: ViewModel() {
    private val currentPictureUri = MutableLiveData(EMPTY_IMAGE_URI)

    val uiState = GalleryTabUiState(
        currentPictureUri = currentPictureUri
    )

    fun changeCurrentPicture(uri: Uri) {
        currentPictureUri.value = uri
    }
}
