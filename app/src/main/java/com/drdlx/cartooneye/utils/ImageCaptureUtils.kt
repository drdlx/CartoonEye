package com.drdlx.cartooneye.utils

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.ImageFormat.NV21
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.drdlx.cartooneye.startScreen.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun ImageCapture.takePicture(executor: Executor): File {
    val photoFile = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            File.createTempFile("image", "jpg")
        }.getOrElse { ex ->
            Log.e("TakePicture", "Failed to create temporary file", ex)
            File("/dev/null")
        }
    }

    return suspendCoroutine { continuation ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        takePicture(
            outputOptions, executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.i("TakePicture", "Successfully captured!")
                    Log.i("TakePicture", photoFile.absolutePath)
                    continuation.resume(photoFile)
                }

                override fun onError(ex: ImageCaptureException) {
                    Log.e("TakePicture", "Image capture failed", ex)
                    continuation.resumeWithException(ex)
                }
            }
        )
    }
}

suspend fun makeTemporaryPicture(bitmap: Bitmap): File = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val file = File.createTempFile("image", "jpg")
            val fileOutputStream = file.outputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            file
        }.getOrElse { ex ->
            Log.e("TakePicture", "Failed to create temporary image file", ex)
            File("/dev/null")
        }

}
