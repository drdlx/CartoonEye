package com.drdlx.cartooneye.tabScreens.galleryTabScreen.model

import android.net.Uri
import androidx.lifecycle.LiveData

data class GalleryTabUiState(
    val currentPictureUri: LiveData<Uri>
)
