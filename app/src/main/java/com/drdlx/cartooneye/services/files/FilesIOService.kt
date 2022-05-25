package com.drdlx.cartooneye.services.files

import android.content.ContentResolver
import android.graphics.Bitmap
import org.koin.core.annotation.Single

interface FilesIOService {

    fun savePhoto(
        bitmap: Bitmap,
        contentResolver: ContentResolver?,
    )
}