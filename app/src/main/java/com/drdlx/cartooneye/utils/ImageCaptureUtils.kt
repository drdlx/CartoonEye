package com.drdlx.cartooneye.utils

import androidx.camera.core.ImageCapture
import java.io.File
import java.util.concurrent.Executor
import android.util.Log
import androidx.camera.core.ImageCaptureException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
