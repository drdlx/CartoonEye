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

/*private fun createMp4File(): Uri? {
    // Since we use legacy external storage for Android 10,
    // we still need to request for storage permission on Android 10.
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        if (!checkAndRequestStoragePermission()) {
            Log.i(TAG, String.format(
                "Didn't createMp4File. No storage permission, API Level = %d",
                Build.VERSION.SDK_INT));
            return null;
        }
    }

    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
    val mp4FileName = "arcore-" + dateFormat.format(Date()).toString() + ".mp4"
    val resolver = this.contentResolver
    var videoCollection: Uri? = null
    videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    // Create a new Media file record.
    val newMp4FileDetails = ContentValues()
    newMp4FileDetails.put(MediaStore.Video.Media.DISPLAY_NAME, mp4FileName)
    newMp4FileDetails.put(MediaStore.Video.Media.MIME_TYPE, MP4_VIDEO_MIME_TYPE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // The Relative_Path column is only available since API Level 29.
        newMp4FileDetails.put(
            MediaStore.Video.Media.RELATIVE_PATH,
            Environment.DIRECTORY_MOVIES
        )
    } else {
        // Use the Data column to set path for API Level <= 28.
        val mp4FileDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val absoluteMp4FilePath = File(mp4FileDir, mp4FileName).absolutePath
        newMp4FileDetails.put(MediaStore.Video.Media.DATA, absoluteMp4FilePath)
    }
    val newMp4FileUri = resolver.insert(videoCollection, newMp4FileDetails)

    // Ensure that this file exists and can be written.
    if (newMp4FileUri == null) {
        Log.e(
            TAG,
            String.format(
                "Failed to insert Video entity in MediaStore. API Level = %d",
                Build.VERSION.SDK_INT
            )
        )
        return null
    }

    // This call ensures the file exist before we pass it to the ARCore API.
    if (!testFileWriteAccess(newMp4FileUri)) {
        return null
    }
    Log.d(
        TAG,
        String.format(
            "createMp4File = %s, API Level = %d",
            newMp4FileUri,
            Build.VERSION.SDK_INT
        )
    )
    return newMp4FileUri
}

// Test if the file represented by the content Uri can be open with write access.
private fun testFileWriteAccess(contentUri: Uri): Boolean {
    try {
        this.contentResolver.openOutputStream(contentUri).use { mp4File ->
            Log.d(
                MainActivity.TAG,
                String.format("Success in testFileWriteAccess %s", contentUri.toString())
            )
            return true
        }
    } catch (e: FileNotFoundException) {
        Log.e(
            MainActivity.TAG,
            String.format(
                "FileNotFoundException in testFileWriteAccess %s",
                contentUri.toString()
            ),
            e
        )
    } catch (e: IOException) {
        Log.e(
            MainActivity.TAG,
            String.format("IOException in testFileWriteAccess %s", contentUri.toString()),
            e
        )
    }
    return false
}*/
