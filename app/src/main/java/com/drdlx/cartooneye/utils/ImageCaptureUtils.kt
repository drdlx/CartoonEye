package com.drdlx.cartooneye.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat.NV21
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
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
            Log.e("TakePicture", "Failed to create temporary file", ex)
            File("/dev/null")
        }

}

fun jpegByteArrayFrom(yuv420_888: Image): ByteArray =
    yuv420_888.nv21ByteArray
        .let { YuvImage(it, NV21, yuv420_888.width, yuv420_888.height, null) }
        .getJpegDataWithQuality(100)

private val Image.nv21ByteArray
    get() = ByteArray(width * height * 3 / 2).also {
        val vPlane = planes[2]
        val y = planes[0].buffer.apply { rewind() }
        val u = planes[1].buffer.apply { rewind() }
        val v = vPlane.buffer.apply { rewind() }
        y.get(it, 0, y.capacity()) // copy Y components
        if (vPlane.pixelStride == 2) {
            // Both of U and V are interleaved data, so copying V makes VU series but last U
            v.get(it, y.capacity(), v.capacity())
            it[it.size - 1] = u.get(u.capacity() - 1) // put last U
        } else { // vPlane.pixelStride == 1
            var offset = it.size - 1
            var i = v.capacity()
            while (i-- != 0) { // make VU interleaved data into ByteArray
                it[offset - 0] = u[i]
                it[offset - 1] = v[i]
                offset -= 2
            }
        }
    }

private fun YuvImage.getJpegDataWithQuality(quality: Int) =
    ByteArrayOutputStream().also {
        compressToJpeg(Rect(0, 0, width, height), quality, it)
    }.toByteArray()
